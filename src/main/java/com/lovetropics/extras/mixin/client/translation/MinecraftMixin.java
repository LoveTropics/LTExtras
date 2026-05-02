package com.lovetropics.extras.mixin.client.translation;

import com.lovetropics.extras.ExtrasConfig;
import com.lovetropics.extras.translation.TranslationPromptScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Function;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow
    @Final
    public Font font;

    @Shadow
    public abstract void setScreen(@Nullable Screen screen);

    @Inject(method = "addInitialScreens", at = @At("RETURN"))
    private void showPrompt(List<Function<Runnable, Screen>> output, CallbackInfoReturnable<Boolean> cir) {
        if (!ExtrasConfig.TRANSLATION.prompted.get()) {
            output.add(onClose -> new TranslationPromptScreen(onClose, (Minecraft) (Object) this, font));
        }
    }
}
