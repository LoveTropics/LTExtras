package com.lovetropics.extras.schedule;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.network.message.ServerboundSetTimeZonePacket;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.time.ZoneId;

@EventBusSubscriber(modid = LTExtras.MODID, value = Dist.CLIENT)
public class TimeZoneSender {
    @SubscribeEvent
    public static void onLogIn(final ClientPlayerNetworkEvent.LoggingIn event) {
        sendTimeZoneToServer();
    }

    // When the server recreates the player, we can't be sure that it kept our time zone - send it again to be sure
    @SubscribeEvent
    public static void onPlayerClone(final ClientPlayerNetworkEvent.Clone event) {
        sendTimeZoneToServer();
    }

    private static void sendTimeZoneToServer() {
        PacketDistributor.sendToServer(new ServerboundSetTimeZonePacket(ZoneId.systemDefault()));
    }
}
