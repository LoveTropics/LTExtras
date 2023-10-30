package com.lovetropics.extras.translation;

import net.minecraft.network.chat.PlayerChatMessage;

public interface TranslatableChatMessage {
    void ltextras$addTranslations(TranslationBundle bundle);

    PlayerChatMessage ltextras$translate(String language);

    boolean ltextras$hasTranslationFor(String language);
}
