package com.lovetropics.extras.translation;

import com.lovetropics.extras.ExtrasConfig;
import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.network.LTExtrasNetwork;
import com.lovetropics.extras.network.SetTranslationSettingsPacket;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = LTExtras.MODID, value = Dist.CLIENT)
public class TranslationOptionsSender {
    @SubscribeEvent
    public static void onLogIn(final ClientPlayerNetworkEvent.LoggingIn event) {
        sendOptionsToServer();
    }

    // When the server recreates the player, we can't be sure that it kept our state - send it again to be sure
    @SubscribeEvent
    public static void onPlayerClone(final ClientPlayerNetworkEvent.Clone event) {
        sendOptionsToServer();
    }

    private static void sendOptionsToServer() {
        final ExtrasConfig.CategoryTranslation config = ExtrasConfig.TRANSLATION;
        LTExtrasNetwork.CHANNEL.sendToServer(new SetTranslationSettingsPacket(config.translateIncoming.get(), config.translateOutgoing.get()));
    }
}
