package com.lovetropics.extras.shield;

import com.lovetropics.extras.data.attachment.ExtraAttachments;
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
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.List;

@EventBusSubscriber
public final class Shields {
    public static final RoleOverrideType<Boolean> BYPASS = RoleOverrideType.register("shield_bypass", Codec.BOOL);

    public static final double DEFAULT_RADIUS = 4.0;

    private static final int PUSH_INTERVAL_TICKS = 2;
    private static final double MIN_PUSH = 0.3;
    private static final double MAX_PUSH = 1.6;
    private static final double PUSH_PER_BLOCK = 0.5;

    public static void init() {
        //Init so RoleOverride gets called and shield_bypass registered early-ish (or someone has to use the actual command?)
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player entity = event.getEntity();
        if (entity.level() instanceof ServerLevel level
                && entity instanceof ServerPlayer serverPlayer
                && entity.tickCount % PUSH_INTERVAL_TICKS == 0
                && entity.hasData(ExtraAttachments.SHIELD_RADIUS)) {
            tickShield(level, serverPlayer, entity.getData(ExtraAttachments.SHIELD_RADIUS));
        }
    }

    private static void tickShield(ServerLevel level, ServerPlayer shieldedPlayer, double radius) {
        if (shieldedPlayer.isSpectator()) {
            return;
        }
        for (ServerPlayer intruder : level.players()) {
            if (intruder != shieldedPlayer && shouldPush(shieldedPlayer, intruder, radius)) {
                push(shieldedPlayer, intruder, radius);
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

    private static void push(ServerPlayer shielded, ServerPlayer intruder, double radius) {
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

    public static boolean canBypass(ServerPlayer player) {
        return PermissionsApi.lookup().byEntity(player).overrides().test(BYPASS);
    }

    public static boolean isShielded(ServerPlayer serverPlayer) {
        return serverPlayer.hasData(ExtraAttachments.SHIELD_RADIUS);
    }

    public static void setShield(ServerPlayer serverPlayer, double radius) {
        if (radius < 0) {
            removeShield(serverPlayer);
        } else {
            serverPlayer.setData(ExtraAttachments.SHIELD_RADIUS, radius);
        }
    }

    public static void removeShield(ServerPlayer serverPlayer) {
        serverPlayer.removeData(ExtraAttachments.SHIELD_RADIUS);
    }

    public static double getRadius(ServerPlayer serverPlayer) {
        return serverPlayer.getData(ExtraAttachments.SHIELD_RADIUS);
    }

    public static List<ServerPlayer> getShieldedPlayers(MinecraftServer server) {
        return server.getPlayerList().getPlayers()
                .stream()
                .filter(Shields::isShielded)
                .toList();
    }
}
