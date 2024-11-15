package com.lovetropics.extras.mixin.client.menu;

import com.lovetropics.extras.LTExtras;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.LogoRenderer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LogoRenderer.class)
public class LogoRendererMixin {
	@Unique
	private static final ResourceLocation LOVE_TROPICS_TEXTURE = LTExtras.location("textures/gui/title.png");

	@ModifyConstant(method = "renderLogo(Lnet/minecraft/client/gui/GuiGraphics;IF)V")
	private int modifyDefaultHeight(int defaultHeight) {
		return defaultHeight - 10;
	}

	@Inject(method = "renderLogo(Lnet/minecraft/client/gui/GuiGraphics;IFI)V", at = @At("HEAD"), cancellable = true)
	private void renderLogo(GuiGraphics graphics, int screenWidth, float transparency, int height, CallbackInfo ci) {
		ci.cancel();

		RenderSystem.enableBlend();
		graphics.setColor(1.0f, 1.0f, 1.0f, transparency);
		int x = (screenWidth - 256) / 2;
		graphics.blit(LOVE_TROPICS_TEXTURE, x, height, 0.0f, 0.0f, 256, 38, 256, 64);
		graphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		RenderSystem.disableBlend();
	}
}
