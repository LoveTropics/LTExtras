package com.lovetropics.extras.schedule;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.data.attachment.ExtraAttachments;
import net.minecraft.resources.ResourceLocation;
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
    public static void onPlayerClone(final PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            final Player oldPlayer = event.getOriginal();

            PlayerTimeZone oldTimeZone = getOrDefault(oldPlayer);
            PlayerTimeZone newTimeZone = getOrDefault(event.getEntity());

            newTimeZone.zoneId = oldTimeZone.zoneId;
        }
    }

    public static void set(final ServerPlayer player, final ZoneId zone) {
        final PlayerTimeZone capability = getOrDefault(player);
        capability.zoneId = zone;
    }

    public static ZoneId get(final ServerPlayer player) {
        final PlayerTimeZone capability = getOrDefault(player);
        return capability.zoneId;
    }

    @NotNull
    private static PlayerTimeZone getOrDefault(final Player player) {
        return player.getData(ExtraAttachments.TIME_ZONE);
    }
}
