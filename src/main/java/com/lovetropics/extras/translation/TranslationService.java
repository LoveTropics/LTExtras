package com.lovetropics.extras.translation;

import com.google.common.collect.ImmutableMap;
import com.lovetropics.extras.ExtrasConfig;
import com.mojang.logging.LogUtils;
import net.minecraft.Util;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class TranslationService {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .executor(Util.ioPool())
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public static final TranslationService INSTANCE = new TranslationService(ExtrasConfig.TECH_STACK.translationUrl);

    private final Supplier<String> url;

    private TranslationService(final Supplier<String> url) {
        this.url = url;
    }

    private boolean isDisabled() {
        return Strings.isBlank(url.get());
    }

    public CompletableFuture<TranslationBundle> translate(final String language, final String text) {
        final TranslatableLanguage languageType = TranslatableLanguage.CODEC.byName(language);
        if (languageType == null || isDisabled()) {
            return CompletableFuture.completedFuture(new TranslationBundle(Map.of(language, text)));
        }
        return switch (languageType) {
            case ENGLISH ->
                    translateTo(text, TranslationMode.EN_ES).thenCombine(translateTo(text, TranslationMode.EN_FR),
                            (spanish, french) -> createBundle(text, spanish, french)
                    );
            case FRENCH ->
                    translateTo(text, TranslationMode.FR_EN).thenCompose(english -> translateTo(english, TranslationMode.EN_ES)
                            .thenApply(spanish -> createBundle(english, spanish, text))
                    );
            case SPANISH ->
                    translateTo(text, TranslationMode.ES_EN).thenCompose(english -> translateTo(english, TranslationMode.EN_FR)
                            .thenApply(french -> createBundle(english, text, french))
                    );
        };
    }

    private static TranslationBundle createBundle(@Nullable final String english, @Nullable final String spanish, @Nullable final String french) {
        final ImmutableMap.Builder<String, String> stringsByLanguage = ImmutableMap.builderWithExpectedSize(3);
        if (english != null) {
            stringsByLanguage.put(TranslatableLanguage.ENGLISH.getSerializedName(), english);
        }
        if (spanish != null) {
            stringsByLanguage.put(TranslatableLanguage.SPANISH.getSerializedName(), spanish);
        }
        if (french != null) {
            stringsByLanguage.put(TranslatableLanguage.FRENCH.getSerializedName(), french);
        }
        return new TranslationBundle(stringsByLanguage.build());
    }

    private CompletableFuture<String> translateTo(final String text, final TranslationMode mode) {
        final HttpRequest request = HttpRequest.newBuilder(URI.create(url.get() + "/" + mode.getSerializedName()))
                .POST(HttpRequest.BodyPublishers.ofString(text))
                .build();
        return HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() >= 200 && response.statusCode() < 300) {
                        return response.body();
                    } else {
                        LOGGER.warn("Received unexpected response {} from translation of '{}' ({}): {}", response.statusCode(), text, mode, response.body());
                        return null;
                    }
                })
                .handle((result, throwable) -> {
                    if (throwable != null) {
                        LOGGER.warn("Failed to translate text: '{}' ({})", text, mode, throwable);
                    }
                    return result;
                });
    }
}
