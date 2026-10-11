package com.lovetropics.extras.zipline;

import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.InterpolationHandler;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PlayerRideable;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

/** Server-controlled invisible carriage. Its saved state also supports reconnecting mid-ride. */
public class ZiplineRideEntity extends Entity implements PlayerRideable {
    private static final double SPEED = 0.35;
    private static final int END_WAIT_TICKS = 20;
    private final InterpolationHandler interpolation = new InterpolationHandler(this, 2);
    private @Nullable UUID cableId;
    private BlockPos start = BlockPos.ZERO;
    private BlockPos end = BlockPos.ZERO;
    private double progress;
    private double target;
    private int endWaitTicks = -1;

    public ZiplineRideEntity(EntityType<?> type, Level level) {
        super(type, level);
        setNoGravity(true);
    }

    public void initialize(ZiplineEntity cable, double from, double to) {
        cableId = cable.getUUID();
        start = cable.startAnchor();
        end = cable.endAnchor();
        progress = from;
        target = to;
        endWaitTicks = -1;
        setPos(path().riderPosition(progress));
    }

    private ZiplinePath path() {
        return new ZiplinePath(ZiplineEntity.attachment(start), ZiplineEntity.attachment(end));
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide()) {
            interpolation.interpolate();
            return;
        }
        if (!(getFirstPassenger() instanceof ServerPlayer rider)) {
            // Allow the saved passenger tree to attach before removing an orphaned carriage.
            if (tickCount > 20) {
                discard();
            }
            return;
        }
        rider.resetFallDistance();
        ZiplinePath path = path();
        if (!path.isValid() || cableId == null) {
            finish(rider);
            return;
        }
        ServerLevel serverLevel = (ServerLevel) level();
        ZiplineChunkTickets.refresh(serverLevel, start, end);
        Entity cable = serverLevel.getEntity(cableId);
        if (!(cable instanceof ZiplineEntity zipline)) {
            // Entity data can arrive after the blocks when restoring a saved ride.
            if (serverLevel.areEntitiesActuallyLoadedAndTicking(ChunkPos.containing(BlockPos.containing(path.point(0.5))))) {
                finish(rider);
            }
            return;
        }
        if (!zipline.anchorsLoaded()) {
            return; // Loading is not destruction. Keep both the target and endpoint timer intact.
        }
        if (!zipline.anchorsValid()) {
            finish(rider);
            return;
        }
        double speed = SPEED * path.speedMultiplier(target > progress);
        double next = progress + Math.copySign(Math.min(speed / path.length(), Math.abs(target - progress)), target - progress);
        // Preserve target while blocked, so removing the obstacle resumes the original direction.
        progress = ZiplineSafety.moveProgress(level(), rider, path, progress, next);
        setPos(path.riderPosition(progress));
        if (Math.abs(progress - target) < 1.0E-9) {
            // Arrival starts the timer; hold the hanging position for one second before dropping.
            if (++endWaitTicks >= END_WAIT_TICKS) {
                finish(rider);
            }
        }
    }

    private void finish(ServerPlayer rider) {
        // Keep the carriage alive until vanilla has requested our exact dismount position.
        rider.stopRiding();
        if (!isVehicle()) {
            discard();
        }
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return false;
    }

    @Override
    public void push(Entity entity) {
        // The carriage never pushes other entities or receives their collision impulses.
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity rider) {
        rider.resetFallDistance();
        rider.setDeltaMovement(Vec3.ZERO);
        return rider.position();
    }

    @Override
    protected void positionRider(Entity passenger, MoveFunction moveFunction) {
        moveFunction.accept(passenger, getX(), getY(), getZ());
        passenger.resetFallDistance();
    }

    @Override
    public Vec3 getPassengerRidingPosition(Entity passenger) {
        return position().add(passenger.getVehicleAttachmentPoint(this));
    }

    @Override
    public InterpolationHandler getInterpolation() {
        return interpolation;
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        return false;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        cableId = input.read("cable", UUIDUtil.CODEC).orElse(null);
        start = input.read("start", BlockPos.CODEC).orElse(BlockPos.ZERO);
        end = input.read("end", BlockPos.CODEC).orElse(BlockPos.ZERO);
        progress = input.getDoubleOr("progress", 0.0);
        target = input.getDoubleOr("target", 1.0);
        endWaitTicks = input.getIntOr("end_wait_ticks", -1);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        output.storeNullable("cable", UUIDUtil.CODEC, cableId);
        output.store("start", BlockPos.CODEC, start);
        output.store("end", BlockPos.CODEC, end);
        output.putDouble("progress", progress);
        output.putDouble("target", target);
        output.putInt("end_wait_ticks", endWaitTicks);
    }
}
