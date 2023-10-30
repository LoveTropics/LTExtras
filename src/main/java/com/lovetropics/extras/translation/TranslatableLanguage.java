package com.lovetropics.extras.translation;

import javax.annotation.Nullable;

public enum TranslatableLanguage {
    ENGLISH,
    SPANISH,
    FRENCH,
    ;

    @Nullable
    public static TranslatableLanguage byKey(final String key) {
        if (key.startsWith("en_")) {
            return ENGLISH;
        } else if (key.startsWith("es_")) {
            return SPANISH;
        } else if (key.startsWith("fr_")) {
            return FRENCH;
        }
        return null;
    }
}
