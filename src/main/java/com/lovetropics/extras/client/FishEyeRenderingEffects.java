package com.lovetropics.extras.client;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.effect.ExtraEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = LTExtras.MODID, value = Dist.CLIENT)
public class FishEyeRenderingEffects {
    private static final Minecraft CLIENT = Minecraft.getInstance();

    @SubscribeEvent
    public static void onRenderFog(EntityViewRenderEvent.RenderFogEvent event) {
        LocalPlayer player = CLIENT.player;
        if (player != null && player.hasEffect(ExtraEffects.FISH_EYE.get())) {
            event.scaleNearPlaneDistance(1.25f);
            event.scaleFarPlaneDistance(1.25f);
            event.setCanceled(true);
        }
    }
}
