package com.lovetropics.extras.mixin.translation;

import com.lovetropics.extras.ExtraLangKeys;
import com.lovetropics.extras.translation.TranslatableChatMessage;
import com.lovetropics.extras.translation.TranslationBundle;
import com.lovetropics.extras.translation.TranslationOptions;
import net.minecraft.network.chat.*;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.FilteredText;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.FutureChain;
import net.minecraftforge.common.ForgeHooks;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {
    @Unique
    private static final Component TRANSLATED_MARKER = Component.literal(" \uE041").withStyle(style -> style.withHoverEvent(
            new HoverEvent(HoverEvent.Action.SHOW_TEXT, ExtraLangKeys.MESSAGE_TRANSLATED.get())
    ));

    @Shadow
    @Final
    private MinecraftServer server;
    @Shadow
    public ServerPlayer player;
    @Shadow
    @Final
    private FutureChain chatMessageChain;

    @Shadow
    private static boolean isChatMessageIllegal(final String pMessage) {
        return false;
    }

    @Shadow
    public abstract void disconnect(final Component pTextComponent);

    @Shadow
    protected abstract Optional<LastSeenMessages> tryHandleChat(final String pMessage, final Instant pTimestamp, final LastSeenMessages.Update pUpdate);

    @Shadow
    protected abstract PlayerChatMessage getSignedMessage(final ServerboundChatPacket pPacket, final LastSeenMessages pLastSeenMessages) throws SignedMessageChain.DecodeException;

    @Shadow
    protected abstract CompletableFuture<FilteredText> filterTextPacket(final String pText);

    @Shadow
    protected abstract void handleMessageDecodeFailure(final SignedMessageChain.DecodeException pException);

    @Shadow
    protected abstract void broadcastChatMessage(final PlayerChatMessage pMessage);

    /**
     * @author Gegy
     * @reason No good reason, please FIXME!
     */
    @Overwrite
    public void handleChat(final ServerboundChatPacket packet) {
        if (isChatMessageIllegal(packet.message())) {
            disconnect(Component.translatable("multiplayer.disconnect.illegal_characters"));
            return;
        }
        final Optional<LastSeenMessages> lastSeen = tryHandleChat(packet.message(), packet.timeStamp(), packet.lastSeenMessages());
        if (lastSeen.isPresent()) {
            server.submit(() -> {
                final PlayerChatMessage message;
                try {
                    message = getSignedMessage(packet, lastSeen.get());
                } catch (final SignedMessageChain.DecodeException e) {
                    handleMessageDecodeFailure(e);
                    return;
                }

                final CompletableFuture<FilteredText> filteredText = filterTextPacket(message.signedContent());
                final CompletableFuture<Component> decoratedText = ForgeHooks.getServerChatSubmittedDecorator().decorate(player, message.decoratedContent());
                // Also request translations at this point, but make sure to not change the order that we distribute chat messages
                final CompletableFuture<TranslationBundle> translations = TranslationOptions.translate(player, message);
                chatMessageChain.append(executor -> CompletableFuture.allOf(filteredText, decoratedText, translations).thenAcceptAsync(unused -> {
                    final Component decoratedContent = decoratedText.join();
                    if (decoratedContent == null) {
                        return;
                    }
                    final PlayerChatMessage decoratedMessage = message.withUnsignedContent(decoratedContent).filter(filteredText.join().mask());
                    ((TranslatableChatMessage) (Object) decoratedMessage).ltextras$addTranslations(translations.join());
                    broadcastChatMessage(decoratedMessage);
                }, executor));
            });
        }
    }

    @ModifyVariable(method = "sendPlayerChatMessage", at = @At("HEAD"), argsOnly = true)
    private ChatType.Bound modifyChatMessageType(final ChatType.Bound chatType, final PlayerChatMessage message) {
        if (!TranslationOptions.shouldTranslateIncoming(player)) {
            return chatType;
        }
        if (((TranslatableChatMessage) (Object) message).ltextras$hasTranslationFor(player.getLanguage())) {
            return new ChatType.Bound(chatType.chatType(), chatType.name().copy().append(TRANSLATED_MARKER), chatType.targetName());
        }
        return chatType;
    }

    @ModifyVariable(method = "sendPlayerChatMessage", at = @At("HEAD"), argsOnly = true)
    private PlayerChatMessage modifyChatMessage(final PlayerChatMessage message) {
        if (!TranslationOptions.shouldTranslateIncoming(player)) {
            return message;
        }
        return ((TranslatableChatMessage) (Object) message).ltextras$translate(player.getLanguage());
    }
}
