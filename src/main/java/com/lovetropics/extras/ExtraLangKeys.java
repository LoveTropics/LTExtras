package com.lovetropics.extras;

import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.providers.ProviderType;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public enum ExtraLangKeys {
    CLUB_INVITE_1_TOP("invite", "club_1.top", "We need you.\nWear a disguise.\nThey are watching."),
    CLUB_INVITE_1_BOTTOM("invite", "club_1.bottom", "Did you know disguises are fireproof?"),
    CLUB_INVITE_2_TOP("invite", "club_2.top", "See you there.\nWear a disguise.\nGrab a drink."),
    CLUB_INVITE_2_BOTTOM("invite", "club_2.bottom", "I hear the Limeade's good."),
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
