package com.lovetropics.extras.mixin.client.chatfix;

import com.llamalad7.mixinextras.sugar.Local;
import com.lovetropics.extras.duck.CommandUsageClickFinder;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.input.MouseButtonEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {
    @Shadow
    private CommandSuggestions commandSuggestions;

    @Inject(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/ActiveTextCollector$ClickableStyleFinder;result()Lnet/minecraft/network/chat/Style;"))
    private void captureClick(MouseButtonEvent event, boolean doubleClick, CallbackInfoReturnable<Boolean> cir, @Local(name = "finder") ActiveTextCollector.ClickableStyleFinder finder) {
        ((CommandUsageClickFinder) commandSuggestions).ltextras$captureClick(finder);
    }
}
