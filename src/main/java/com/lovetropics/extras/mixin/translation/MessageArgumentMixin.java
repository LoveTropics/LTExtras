package com.lovetropics.extras.mixin.translation;

import com.lovetropics.extras.translation.TranslatableChatMessage;
import com.lovetropics.extras.translation.TranslationBundle;
import com.lovetropics.extras.translation.TranslationOptions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.FilteredText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Mixin(MessageArgument.class)
public abstract class MessageArgumentMixin {
    @Shadow
    private static CompletableFuture<FilteredText> filterPlainText(final CommandSourceStack pSource, final PlayerChatMessage pMessage) {
        return null;
    }

    /**
     * @author Gegy
     * @reason also does not need to be, please FIXME!
     */
    @Overwrite
    private static void resolveSignedMessage(final Consumer<PlayerChatMessage> callback, final CommandSourceStack source, final PlayerChatMessage message) {
        final MinecraftServer server = source.getServer();
        final CompletableFuture<FilteredText> filteredText = filterPlainText(source, message);
        final CompletableFuture<Component> decoratedText = server.getChatDecorator().decorate(source.getPlayer(), message.decoratedContent());
        final CompletableFuture<TranslationBundle> translations = TranslationOptions.translate(source.getPlayer(), message);
        source.getChatMessageChainer().append(executor ->
                CompletableFuture.allOf(filteredText, decoratedText, translations).thenAcceptAsync(unused -> {
                    final PlayerChatMessage decoratedMessage = message.withUnsignedContent(decoratedText.join()).filter(filteredText.join().mask());
                    ((TranslatableChatMessage) (Object) decoratedMessage).ltextras$addTranslations(translations.join());
                    callback.accept(decoratedMessage);
                }, executor)
        );
    }
}
