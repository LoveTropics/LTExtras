package com.lovetropics.extras;

import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.providers.ProviderType;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public enum ExtraLangKeys {
    COLLECTIBLE_COMPASS_ALREADY_USED("item", "collectible_compass.already_used", "This Compass already points to a Collectible. If you want to find something else, you will need to find a new one!"),
    COLLECTIBLE_COMPASS_SUCCESS("item", "collectible_compass.success", "\uD83D\uDC40 The Compass has found a Collectible that you don't have yet, and now points towards it."),
    COLLECTIBLE_COMPASS_FAIL("item", "collectible_compass.fail", "The Compass was not able to find any Collectibles that you don't have nearby. Maybe try somewhere else?"),
    CLUB_INVITE_1_TOP("invite", "club_1.top", "We need you.\nWear a disguise.\nThey are watching."),
    CLUB_INVITE_1_BOTTOM("invite", "club_1.bottom", "Did you know disguises are fireproof?"),
    CLUB_INVITE_2_TOP("invite", "club_2.top", "See you there.\nWear a disguise.\nGrab a drink."),
    CLUB_INVITE_2_BOTTOM("invite", "club_2.bottom", "I hear the Limeade's good."),
    MESSAGE_TRANSLATED("chat", "message_translated", "This chat message has been machine-translated, and it may not be accurate. You can see the original message by hovering over the white bar to the left.")
    ;

    private final String key;
    private final String value;

    ExtraLangKeys(final String type, final String key, final String value) {
        this.key = Util.makeDescriptionId(type, new ResourceLocation(LTExtras.MODID, key));
        this.value = value;
    }

    public MutableComponent get() {
        return Component.translatable(key);
    }

    public MutableComponent format(final Object... args) {
        return Component.translatable(key, args);
    }

    public static void init(final Registrate registrate) {
        registrate.addDataGenerator(ProviderType.LANG, prov -> {
            for (final ExtraLangKeys lang : values()) {
                prov.add(lang.key, lang.value);
            }
        });
    }
}
