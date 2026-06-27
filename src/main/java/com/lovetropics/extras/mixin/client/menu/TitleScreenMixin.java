package com.lovetropics.extras.mixin.client.menu;

import com.mojang.realmsclient.gui.screens.RealmsNotificationsScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
    @Shadow
    private @Nullable RealmsNotificationsScreen realmsNotificationsScreen;

    @Inject(method = "init", at = @At("TAIL"))
    private void postInit(CallbackInfo ci) {
        // Realms button is gone, so that's not useful
        realmsNotificationsScreen = null;
    }
}
