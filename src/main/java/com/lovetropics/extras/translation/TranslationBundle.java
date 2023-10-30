package com.lovetropics.extras.translation;

import java.util.Map;

public record TranslationBundle(Map<TranslatableLanguage, String> stringsByLanguage) {
    public static final TranslationBundle EMPTY = new TranslationBundle(Map.of());
}
