package com.lovetropics.extras.client;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.effect.ExtraEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.FogType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ViewportEvent;

@EventBusSubscriber(modid = LTExtras.MODID, value = Dist.CLIENT)
public class FishEyeRenderingEffects {
	private static final Minecraft CLIENT = Minecraft.getInstance();

	@SubscribeEvent
	public static void onRenderFog(ViewportEvent.RenderFog event) {
		LocalPlayer player = CLIENT.player;
		if (player != null && player.hasEffect(ExtraEffects.FISH_EYE)) {
			event.scaleFarPlaneDistance(1.25f);
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onFovChange(ViewportEvent.ComputeFov event) {
		LocalPlayer player = CLIENT.player;
		if (event.getCamera().getFluidInCamera() == FogType.WATER) {
			if (player != null && player.hasEffect(ExtraEffects.FISH_EYE)) {
				double fov = event.getFOV();
				// Undo vanilla FOV reduction when underwater
				event.setFOV(fov / Mth.lerp(CLIENT.options.fovEffectScale().get(), 1.0F, 0.85714287F));
			}
		}
	}
}
