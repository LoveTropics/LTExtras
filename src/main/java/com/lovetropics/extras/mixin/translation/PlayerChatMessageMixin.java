package com.lovetropics.extras.mixin.translation;

import com.lovetropics.extras.translation.TranslatableChatMessage;
import com.lovetropics.extras.translation.TranslatableLanguage;
import com.lovetropics.extras.translation.TranslationBundle;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;

@Mixin(PlayerChatMessage.class)
public abstract class PlayerChatMessageMixin implements TranslatableChatMessage {
    @Shadow
    public abstract String signedContent();

    @Unique
    private final Map<TranslatableLanguage, String> ltextras$translations = new Object2ObjectOpenHashMap<>();

    @Override
    public void ltextras$addTranslations(final TranslationBundle bundle) {
        ltextras$translations.putAll(bundle.stringsByLanguage());
    }

    @Override
    public PlayerChatMessage ltextras$translate(final String language) {
        final PlayerChatMessage self = (PlayerChatMessage) (Object) this;
        final String translation = lTExtras$getTranslationFor(language);
        if (translation != null && !translation.equals(signedContent())) {
            return self.withUnsignedContent(Component.literal(translation));
        }
        return self;
    }

    @Override
    public boolean ltextras$hasTranslationFor(final String language) {
        final String translation = lTExtras$getTranslationFor(language);
        return translation != null && !translation.equals(signedContent());
    }

    @Unique
    @Nullable
    private String lTExtras$getTranslationFor(final String language) {
        final TranslatableLanguage languageType = TranslatableLanguage.byKey(language);
        return languageType != null ? ltextras$translations.get(languageType) : null;
    }
}
