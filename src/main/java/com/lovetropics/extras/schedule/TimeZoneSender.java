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
        LTExtrasNetwork.CHANNEL.sendToServer(new SetTimeZonePacket(ZoneId.systemDefault()));
    }
}
