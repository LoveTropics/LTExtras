package com.lovetropics.extras.mixin.client.chatfix;

import com.lovetropics.extras.ExtraLangKeys;
import com.lovetropics.extras.duck.CommandUsageClickFinder;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonLinks;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(CommandSuggestions.class)
public abstract class CommandSuggestionsMixin implements CommandUsageClickFinder {
    @Shadow
    private boolean messagesAllowed;

    @Shadow
    @Final
    private List<FormattedCharSequence> commandUsage;

    @Shadow
    @Final
    private boolean anchorToBottom;

    @Shadow
    @Final
    private Screen screen;

    @Shadow
    private int commandUsagePosition;

    @Inject(method = "updateCommandInfo", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/CommandSuggestions;recomputeUsageBoxWidth()V"))
    private void updateCommandInfo(CallbackInfo ci) {
        if (messagesAllowed && Minecraft.getInstance().allowChatOnlyWithFriend()) {
            lt$addFriendsOnlyPrompt();
        }
    }

    @Inject(method = "updateUsageInfo", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/CommandSuggestions;recomputeUsageBoxWidth()V"))
    private void updateUsageInfo(ParseResults<ClientSuggestionProvider> currentParse, Suggestions suggestions, CallbackInfo ci) {
        if (messagesAllowed && Minecraft.getInstance().allowChatOnlyWithFriend()) {
            lt$addFriendsOnlyPrompt();
        }
    }

    @Unique
    private void lt$addFriendsOnlyPrompt() {
        Component link = Component.literal(CommonLinks.PRIVACY_AND_ONLINE_SETTINGS.toString())
                .withStyle(ChatFormatting.BLUE, ChatFormatting.UNDERLINE)
                .withStyle(style -> style.withClickEvent(new ClickEvent.OpenUrl(CommonLinks.PRIVACY_AND_ONLINE_SETTINGS)));
        commandUsage.add(ExtraLangKeys.CHAT_FRIENDS_ONLY_LINK.format(link).getVisualOrderText());
        commandUsage.add(ExtraLangKeys.CHAT_FRIENDS_ONLY.get().withStyle(ChatFormatting.RED).getVisualOrderText());
    }

    /// Cloned from [CommandSuggestions#extractUsage]
    @Override
    public void ltextras$captureClick(ActiveTextCollector.ClickableStyleFinder finder) {
        int y = 0;
        for (FormattedCharSequence line : commandUsage) {
            int lineY;
            if (anchorToBottom) {
                lineY = screen.height - CommandSuggestions.USAGE_OFFSET_FROM_BOTTOM - CommandSuggestions.LINE_HEIGHT * y;
            } else {
                lineY = 72 + CommandSuggestions.LINE_HEIGHT * y;
            }
            finder.accept(commandUsagePosition, lineY + 2, line);
            y++;
        }
    }
}
