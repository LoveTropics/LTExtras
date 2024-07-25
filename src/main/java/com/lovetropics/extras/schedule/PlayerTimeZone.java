package com.lovetropics.extras.schedule;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.data.attachment.ExtraAttachments;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

import java.time.ZoneId;
import java.time.ZoneOffset;

@EventBusSubscriber(modid = LTExtras.MODID)
public class PlayerTimeZone {
    private ZoneId zoneId = ZoneOffset.UTC;

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            Player oldPlayer = event.getOriginal();

            PlayerTimeZone oldTimeZone = getOrDefault(oldPlayer);
            PlayerTimeZone newTimeZone = getOrDefault(event.getEntity());

            newTimeZone.zoneId = oldTimeZone.zoneId;
        }
    }

    public static void set(ServerPlayer player, ZoneId zone) {
        PlayerTimeZone capability = getOrDefault(player);
        capability.zoneId = zone;
    }

    public static ZoneId get(ServerPlayer player) {
        PlayerTimeZone capability = getOrDefault(player);
        return capability.zoneId;
    }

    @NotNull
    private static PlayerTimeZone getOrDefault(Player player) {
        return player.getData(ExtraAttachments.TIME_ZONE);
    }
}
