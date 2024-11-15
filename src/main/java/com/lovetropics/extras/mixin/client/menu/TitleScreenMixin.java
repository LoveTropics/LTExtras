package com.lovetropics.extras.mixin.client.menu;

import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
	@Inject(method = "realmsNotificationsEnabled", at = @At("HEAD"), cancellable = true)
	private void realmsNotificationsEnabled(CallbackInfoReturnable<Boolean> cir) {
		// Realms button is gone, so that's not useful
		cir.setReturnValue(false);
	}
}
