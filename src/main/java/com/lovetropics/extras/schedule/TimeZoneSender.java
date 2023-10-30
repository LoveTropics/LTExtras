package com.lovetropics.extras.schedule;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.network.LTExtrasNetwork;
import com.lovetropics.extras.network.SetTimeZonePacket;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.time.ZoneId;

@Mod.EventBusSubscriber(modid = LTExtras.MODID, value = Dist.CLIENT)
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
        LTExtrasNetwork.CHANNEL.sendToServer(new SetTimeZonePacket(ZoneId.systemDefault()));
    }
}
