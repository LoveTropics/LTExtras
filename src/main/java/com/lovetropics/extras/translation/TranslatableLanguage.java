package com.lovetropics.extras.translation;

import net.minecraft.util.StringRepresentable;

public enum TranslatableLanguage implements StringRepresentable {
    ENGLISH("en_us"),
    SPANISH("es_es"),
    FRENCH("fr_fr"),
    ;

    public static final EnumCodec<TranslatableLanguage> CODEC = StringRepresentable.fromEnum(TranslatableLanguage::values);

    private final String key;

    TranslatableLanguage(final String key) {
        this.key = key;
    }

    @Override
    public String getSerializedName() {
        return key;
    }
}
