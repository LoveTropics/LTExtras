package com.lovetropics.extras.techstack;

import com.lovetropics.lib.techstack.Crud;
import com.lovetropics.lib.techstack.TechstackEventSubscriber;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import java.net.URI;
import java.net.URISyntaxException;

public class ExtrasTechstackSubscriber {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static @Nullable TechstackEventSubscriber subscriber;

    private ExtrasTechstackSubscriber() {
    }

    public static void updateConfig(String uri, String token) {
        if (subscriber != null) {
            subscriber.close();
        }
        subscriber = buildSubscriber(uri, token);
    }

    private static @Nullable TechstackEventSubscriber buildSubscriber(String uriString, String token) {
        if (uriString.isBlank() || token.isBlank()) {
            return null;
        }

        URI uri;
        try {
            uri = new URI(uriString);
        } catch (URISyntaxException e) {
            LOGGER.warn("Malformed URI", e);
            return null;
        }

        TechstackEventSubscriber.Builder subscriber = TechstackEventSubscriber.builder(uri)
                .authenticate(token);

        addEventSubscriptions(subscriber);

        return subscriber.build();
    }

    private static void addEventSubscriptions(TechstackEventSubscriber.Builder subscriber) {
        subscriber.subscribe(Crud.CREATE, "video_upload_game", VideoUpload.CODEC, video ->
                VideoImporter.get().importVideo(video.title, video.url, video.duration)
        );
    }

    private record VideoUpload(
            String title,
            URI url,
            double duration
    ) {
        public static final Codec<VideoUpload> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.STRING.fieldOf("title").forGetter(VideoUpload::title),
                ExtraCodecs.UNTRUSTED_URI.fieldOf("url").forGetter(VideoUpload::url),
                Codec.DOUBLE.fieldOf("duration").forGetter(VideoUpload::duration)
        ).apply(i, VideoUpload::new));
    }
}
