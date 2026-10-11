package com.lovetropics.extras.zipline;

import com.lovetropics.extras.ExtraBlocks;
import com.lovetropics.extras.ExtraLangKeys;
import com.lovetropics.extras.entity.ExtraEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/** One persistent entity per cable; it neither collides nor intercepts vanilla entity picking. */
public class ZiplineEntity extends Entity {
    private static final EntityDataAccessor<BlockPos> START = SynchedEntityData.defineId(ZiplineEntity.class, EntityDataSerializers.BLOCK_POS);
    private static final EntityDataAccessor<BlockPos> END = SynchedEntityData.defineId(ZiplineEntity.class, EntityDataSerializers.BLOCK_POS);

    public ZiplineEntity(EntityType<?> type, Level level) {
        super(type, level);
        setNoGravity(true);
    }

    public void setAnchors(BlockPos start, BlockPos end) {
        entityData.set(START, start.immutable());
        entityData.set(END, end.immutable());
        setPos(path().point(0.5));
    }

    public BlockPos startAnchor() {
        return entityData.get(START);
    }

    public BlockPos endAnchor() {
        return entityData.get(END);
    }

    public ZiplinePath path() {
        return new ZiplinePath(attachment(startAnchor()), attachment(endAnchor()));
    }

    public static Vec3 attachment(BlockPos pos) {
        return Vec3.atBottomCenterOf(pos).add(0.0, 0.875, 0.0);
    }

    public static @Nullable ZiplineEntity atAnchor(Level level, BlockPos pos) {
        for (ZiplineEntity cable : level.getEntitiesOfClass(ZiplineEntity.class, new AABB(pos).inflate(ZiplinePath.MAX_LENGTH))) {
            if (cable.startAnchor().equals(pos) || cable.endAnchor().equals(pos)) {
                return cable;
            }
        }
        return null;
    }

    public boolean anchorsLoaded() {
        if (level() instanceof ServerLevel serverLevel) {
            return ZiplineChunkTickets.isLoaded(serverLevel, startAnchor()) && ZiplineChunkTickets.isLoaded(serverLevel, endAnchor());
        }
        return level().hasChunkAt(startAnchor()) && level().hasChunkAt(endAnchor());
    }

    public boolean anchorsValid() {
        return anchorsLoaded() && level().getBlockState(startAnchor()).is(ExtraBlocks.ZIPLINE_ANCHOR.get())
                && level().getBlockState(endAnchor()).is(ExtraBlocks.ZIPLINE_ANCHOR.get());
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide() && (!path().isValid()
                || level().hasChunkAt(startAnchor()) && !level().getBlockState(startAnchor()).is(ExtraBlocks.ZIPLINE_ANCHOR.get())
                || level().hasChunkAt(endAnchor()) && !level().getBlockState(endAnchor()).is(ExtraBlocks.ZIPLINE_ANCHOR.get()))) {
            discard();
        }
    }

    public void board(ServerPlayer player) {
        board(player, player.position(), false);
    }

    public void boardFromAnchor(ServerPlayer player, BlockPos anchor) {
        if (anchor.equals(startAnchor()) || anchor.equals(endAnchor())) {
            board(player, attachment(anchor), false);
        }
    }

    /** clickedProgress comes from the server's validated cable raycast, never from the packet. */
    public void boardFromCable(ServerPlayer player, double clickedProgress) {
        board(player, path().point(clickedProgress), true);
    }

    private void board(ServerPlayer player, Vec3 clickedPoint, boolean followLook) {
        if (player.isPassenger() || player.isSpectator() || !player.isAlive()) {
            return;
        }
        ZiplinePath path = path();
        if (!path.isValid() || isRemoved()) {
            player.sendSystemMessage(ExtraLangKeys.ZIPLINE_UNAVAILABLE.get(), true);
            return;
        }
        ServerLevel serverLevel = (ServerLevel) level();
        ZiplineChunkTickets.refresh(serverLevel, startAnchor(), endAnchor());
        // Load the two anchor blocks once on interaction, before validating them. The tickets
        // keep them (and the midpoint cable entity) available for the rest of the ride.
        serverLevel.getChunkAt(startAnchor());
        serverLevel.getChunkAt(endAnchor());
        if (!anchorsValid()) {
            player.sendSystemMessage(ExtraLangKeys.ZIPLINE_UNAVAILABLE.get(), true);
            return;
        }
        double clickedProgress = path.nearestProgress(clickedPoint);
        boolean towardEnd = followLook ? path.travelsTowardEnd(clickedProgress, player.getLookAngle()) : clickedProgress <= 0.5;
        var boarding = path.findBoardingProgress(clickedPoint, player.getBbWidth(), towardEnd,
                candidate -> ZiplineSafety.isClear(level(), player, path.riderPosition(candidate)));
        if (boarding.isEmpty()) {
            player.sendSystemMessage(ExtraLangKeys.ZIPLINE_BLOCKED.get(), true);
            return;
        }
        double from = boarding.getAsDouble();
        // Keep the direction selected at the click, even if the search crosses the midpoint.
        double to = path.destinationProgress(from, towardEnd);
        ZiplineRideEntity ride = new ZiplineRideEntity(ExtraEntities.ZIPLINE_RIDE.get(), level());
        ride.initialize(this, from, to);
        if (level().addFreshEntity(ride)) {
            if (player.startRiding(ride)) {
                ride.positionRider(player);
                player.resetFallDistance();
            } else {
                ride.discard();
            }
        }
    }

    /** Repeated on the server; the packet cannot pick a remote line or bypass a wall. */
    public static @Nullable ZiplineEntity pick(Player player) {
        PickResult hit = pickHit(player);
        return hit != null ? hit.cable() : null;
    }

    public static @Nullable PickResult pickHit(Player player) {
        Vec3 eye = player.getEyePosition();
        Vec3 end = eye.add(player.getLookAngle().scale(player.blockInteractionRange()));
        Vec3 blockHit = player.level().clip(new ClipContext(eye, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player)).getLocation();
        double nearestDistance = eye.distanceTo(blockHit);
        PickResult nearest = null;
        for (ZiplineEntity cable : player.level().getEntitiesOfClass(ZiplineEntity.class,
                player.getBoundingBox().inflate(ZiplinePath.MAX_LENGTH + player.blockInteractionRange()))) {
            ZiplinePath.RayHit hit = cable.path().intersect(eye, end);
            if (hit.hits() && hit.distanceAlongRay() < nearestDistance) {
                nearestDistance = hit.distanceAlongRay();
                nearest = new PickResult(cable, hit.cableProgress());
            }
        }
        return nearest;
    }

    public record PickResult(ZiplineEntity cable, double progress) {
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(START, BlockPos.ZERO);
        builder.define(END, BlockPos.ZERO);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        setAnchors(input.read("start", BlockPos.CODEC).orElse(BlockPos.ZERO), input.read("end", BlockPos.CODEC).orElse(BlockPos.ZERO));
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        output.store("start", BlockPos.CODEC, startAnchor());
        output.store("end", BlockPos.CODEC, endAnchor());
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 256.0 * 256.0;
    }
}
