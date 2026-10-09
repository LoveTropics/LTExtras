package com.lovetropics.extras.shield;

import com.lovetropics.lib.permission.PermissionsApi;
import com.lovetropics.lib.permission.role.RoleOverrideType;
import com.mojang.serialization.Codec;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber
public final class Shields {
    public static final RoleOverrideType<Boolean> BYPASS = RoleOverrideType.register("shield_bypass", Codec.BOOL);

    private static final int PUSH_INTERVAL_TICKS = 2;
    private static final double MIN_PUSH = 0.3;
    private static final double MAX_PUSH = 1.6;
    private static final double PUSH_PER_BLOCK = 0.5;

    private static final Map<UUID, Double> SHIELDS = new HashMap<>();

    public static void init() {
        //Init so RoleOverride gets called and shield_bypass registered early-ish (or someone has to use the actual command?)
    }

    public static boolean canBypass(ServerPlayer player) {
        return PermissionsApi.lookup().byEntity(player).overrides().test(BYPASS);
    }

    public static boolean isShielded(Entity entity) {
        return SHIELDS.containsKey(entity.getUUID());
    }

    public static void setShield(Entity entity, double radius) {
        SHIELDS.put(entity.getUUID(), radius);
    }

    public static void removeShield(Entity entity) {
        SHIELDS.remove(entity.getUUID());
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        SHIELDS.clear();
    }

    @SubscribeEvent
    public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
        Entity entity = event.getEntity();

        if (SHIELDS.isEmpty() || event.getLevel().isClientSide() || entity instanceof Player) {
            return;
        }
        Entity.RemovalReason reason = entity.getRemovalReason();
        if (reason != null && reason.shouldDestroy()) {
            SHIELDS.remove(entity.getUUID());
        }
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        MinecraftServer server = event.getServer();
        if (SHIELDS.isEmpty() || server.getTickCount() % PUSH_INTERVAL_TICKS != 0) {
            return;
        }

        for (Map.Entry<UUID, Double> entry : SHIELDS.entrySet()) {
            Entity shielded = findEntity(server, entry.getKey());
            if (shielded != null && shielded.level() instanceof ServerLevel level) {
                tickShield(level, shielded, entry.getValue());
            }
        }
    }

    public static Map<UUID, Double> getShields() {
        return Collections.unmodifiableMap(SHIELDS);
    }

    @Nullable
    public static Entity findEntity(MinecraftServer server, UUID id) {
        for (ServerLevel level : server.getAllLevels()) {
            Entity entity = level.getEntity(id);
            if (entity != null) {
                return entity;
            }
        }
        return null;
    }

    private static void tickShield(ServerLevel level, Entity shielded, double radius) {
        if (shielded.isSpectator()) {
            return;
        }
        for (ServerPlayer intruder : level.players()) {
            if (intruder != shielded && shouldPush(shielded, intruder, radius)) {
                push(shielded, intruder, radius);
            }
        }
    }

    private static boolean shouldPush(Entity shielded, ServerPlayer intruder, double radius) {
        if (intruder.isSpectator() || intruder.isPassengerOfSameVehicle(shielded)) {
            return false;
        }
        if (!shielded.closerThan(intruder, radius)) {
            return false;
        }

        return !isShielded(intruder) && !canBypass(intruder);
    }

    private static void push(Entity shielded, ServerPlayer intruder, double radius) {
        Vec3 delta = intruder.position().subtract(shielded.position());
        double distance = delta.horizontalDistance();
        Vec3 away = distance > Mth.EPSILON
                ? delta.horizontal().normalize()
                : Vec3.directionFromRotation(0.0F, intruder.getYRot()).reverse();

        double strength = Math.clamp((radius - distance) * PUSH_PER_BLOCK, MIN_PUSH, MAX_PUSH);

        Entity target = intruder.getRootVehicle();
        Vec3 motion = target.getDeltaMovement();

        target.setDeltaMovement(away.x * strength, Math.max(motion.y, 0.1), away.z * strength);

        intruder.connection.send(new ClientboundSetEntityMotionPacket(target));
    }
}
