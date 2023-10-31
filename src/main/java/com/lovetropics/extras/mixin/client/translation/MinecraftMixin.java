package com.lovetropics.extras.mixin.client.translation;

import com.lovetropics.extras.ExtrasConfig;
import com.lovetropics.extras.translation.TranslationPromptScreen;
import com.mojang.realmsclient.client.RealmsClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.main.GameConfig;
import net.minecraft.server.packs.resources.ReloadInstance;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow
    @Final
    public Font font;

    @Shadow
    public abstract void setScreen(@Nullable final Screen pGuiScreen);

    @Shadow
    protected abstract void setInitialScreen(final RealmsClient pRealmsClient, final ReloadInstance pReloadInstance, final GameConfig.QuickPlayData pQuickPlayData);

    @Inject(method = "setInitialScreen", at = @At("HEAD"), cancellable = true)
    private void showPrompt(final RealmsClient realmsClient, final ReloadInstance reloadInstance, final GameConfig.QuickPlayData quickPlayData, final CallbackInfo ci) {
        if (!ExtrasConfig.TRANSLATION.prompted.get()) {
            setScreen(new TranslationPromptScreen(() -> {
                ExtrasConfig.TRANSLATION.prompted.set(true);
                ExtrasConfig.CLIENT_CONFIG.save();
                setInitialScreen(realmsClient, reloadInstance, quickPlayData);
            }, (Minecraft) (Object) this, font));
            ci.cancel();
        }
    }
}
