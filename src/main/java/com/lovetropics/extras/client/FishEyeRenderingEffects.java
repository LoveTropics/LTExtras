package com.lovetropics.extras.client;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.effect.ExtraEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.FogType;
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
			event.scaleFarPlaneDistance(1.25f);
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onFovChange(EntityViewRenderEvent.FieldOfView event) {
		LocalPlayer player = CLIENT.player;
		if (event.getCamera().getFluidInCamera() == FogType.WATER) {
			if (player != null && player.hasEffect(ExtraEffects.FISH_EYE.get())) {
				double fov = event.getFOV();
				// Undo vanilla FOV reduction when underwater
				event.setFOV(fov / Mth.lerp(CLIENT.options.fovEffectScale, 1.0F, 0.85714287F));
			}
		}
	}
}
