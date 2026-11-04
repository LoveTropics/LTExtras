package com.lovetropics.extras.fix;

import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber
public class VoiceChatFix {
    /// SimpleVoiceChat shows the mute icon regardless of whether GUI is hidden - this prevents it.
    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiLayerEvent.Pre event) {
        if (VanillaGuiLayers.HOTBAR.equals(event.getName()) && Minecraft.getInstance().gui.hud.isHidden()) {
            event.setCanceled(true);
        }
    }
}
