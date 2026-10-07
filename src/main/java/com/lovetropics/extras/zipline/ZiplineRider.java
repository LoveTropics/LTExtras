package com.lovetropics.extras.zipline;

import com.lovetropics.extras.data.attachment.ExtraAttachments;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityMountEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.jspecify.annotations.Nullable;

/**
 * Attached to entities riding a zipline. Players simulate their own ride on their client, and the server only uses
 * this to stop punishing them for hanging in the air. Other entities are moved along the rope by the server.
 */
@EventBusSubscriber
public final class ZiplineRider {
    private static final double JUMP_OFF_BOOST = 0.3;
    private static final int AUTO_ATTACH_COOLDOWN = 20;
    private static final double MAX_ROPE_DISTANCE = 8.0;

    private @Nullable ZiplineRide ride;
    private long detachedTime = -AUTO_ATTACH_COOLDOWN;

    public static @Nullable ZiplineRide getRide(Entity entity) {
        ZiplineRider rider = entity.getExistingDataOrNull(ExtraAttachments.ZIPLINE_RIDER);
        return rider != null ? rider.ride : null;
    }

    public static boolean isRiding(Entity entity) {
        return getRide(entity) != null;
    }

    public static void startRiding(Entity entity, ZiplineRide ride) {
        entity.getData(ExtraAttachments.ZIPLINE_RIDER).ride = ride;
        entity.noPhysics = true;
    }

    public static void stopRiding(Entity entity) {
        ZiplineRider rider = entity.getExistingDataOrNull(ExtraAttachments.ZIPLINE_RIDER);
        if (rider != null && rider.ride != null) {
            rider.ride = null;
            rider.detachedTime = entity.level().getGameTime();
            entity.noPhysics = entity.isSpectator();
        }
    }

    /**
     * Whether a mob walking into a pole should be put on the zipline. Players have to click instead.
     */
    public static boolean canAutoAttach(LivingEntity entity) {
        if (!(entity instanceof Mob) || !entity.isAlive() || !entity.isEffectiveAi() || entity.isPassenger() || entity.isVehicle()) {
            return false;
        }
        ZiplineRider rider = entity.getExistingDataOrNull(ExtraAttachments.ZIPLINE_RIDER);
        return rider == null || rider.ride == null && entity.level().getGameTime() - rider.detachedTime >= AUTO_ATTACH_COOLDOWN;
    }

    /**
     * The server can't simulate a player's ride, so it only checks that they stay close to the rope they claim to be on.
     */
    public static boolean isNearRope(Entity entity, ZiplineSegment segment) {
        Vec3 ropePosition = entity.position().add(0.0, ZiplineRide.hangDistance(entity), 0.0);
        return segment.distanceToSqr(ropePosition) <= MAX_ROPE_DISTANCE * MAX_ROPE_DISTANCE;
    }

    /**
     * Replaces vanilla movement for an entity riding a zipline, on the side that controls its movement.
     *
     * @return true if vanilla movement should be skipped
     */
    public static boolean travel(LivingEntity entity, Vec3 input, boolean jumping) {
        ZiplineRide ride = getRide(entity);
        boolean isPlayer = entity instanceof Player;
        if (ride == null || entity.level().isClientSide() != isPlayer) {
            return false;
        }

        // Something else moved the rider, like a teleport: let go without dragging them back to the rope
        if (ride.isDisplaced(entity)) {
            stopRiding(entity);
            return false;
        }

        boolean jumpedOff = isPlayer && jumping;
        boolean leaving = isPlayer && (entity.isShiftKeyDown() || jumping || entity.isSpectator());
        Vec3 moveInput = isPlayer ? getMoveInput(entity, input) : Vec3.ZERO;
        if (leaving || !ride.tick(ZiplineIndex.get(entity.level()), moveInput)) {
            stopRiding(entity);
            Vec3 motion = ride.motion();
            entity.setDeltaMovement(jumpedOff ? motion.add(0.0, JUMP_OFF_BOOST, 0.0) : motion);
            return false;
        }

        ride.positionRider(entity);
        entity.setOnGround(false);
        entity.resetFallDistance();
        return true;
    }

    private static Vec3 getMoveInput(Entity entity, Vec3 input) {
        float yaw = entity.getYRot() * Mth.DEG_TO_RAD;
        float sin = Mth.sin(yaw);
        float cos = Mth.cos(yaw);
        return new Vec3(input.x * cos - input.z * sin, 0.0, input.z * cos + input.x * sin);
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        ZiplineRide ride = getRide(player);
        if (ride == null) {
            return;
        }
        ZiplineSegment segment = ZiplineIndex.get(player.level()).getSegment(ride.segment().from(), ride.segment().to());
        if (segment == null || !isNearRope(player, segment)) {
            stopRiding(player);
            return;
        }
        player.resetFallDistance();
        player.connection.resetFlyingTicks();
    }

    // Vehicles and passengers are moved without going through travel on the zipline, so they would never let go
    @SubscribeEvent
    public static void onEntityMount(EntityMountEvent event) {
        if (event.isMounting()) {
            stopRiding(event.getEntityMounting());
            stopRiding(event.getEntityBeingMounted());
        }
    }
}
