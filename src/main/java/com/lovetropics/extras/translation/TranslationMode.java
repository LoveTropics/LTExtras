package com.lovetropics.extras.translation;

import net.minecraft.util.StringRepresentable;

public enum TranslationMode implements StringRepresentable {
    EN_ES("en/es"),
    ES_EN("es/en"),
    EN_FR("en/fr"),
    FR_EN("fr/en"),
    ;

    private final String key;

    TranslationMode(final String key) {
        this.key = key;
    }

    @Override
    public String getSerializedName() {
        return key;
    }
}
