package com.lovetropics.extras.schedule;

import com.google.common.net.HttpHeaders;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.lovetropics.extras.ExtrasConfig;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Util;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public record StreamSchedule(List<Entry> entries) {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .executor(Util.ioPool())
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public static final Codec<StreamSchedule> CODEC = RecordCodecBuilder.create(i -> i.group(
            Entry.CODEC.listOf().fieldOf("schedule_entries").forGetter(StreamSchedule::entries)
    ).apply(i, StreamSchedule::new));

    public static CompletableFuture<Optional<StreamSchedule>> fetch() {
        String authKey = ExtrasConfig.TECH_STACK.authKey.get();
        if (authKey.isEmpty()) {
            return CompletableFuture.completedFuture(Optional.empty());
        }
        HttpRequest request = HttpRequest.newBuilder(URI.create(ExtrasConfig.TECH_STACK.scheduleUrl.get()))
                .header("Authorization", "Bearer " + authKey)
                .header(HttpHeaders.USER_AGENT, "LTExtras 1.0 (lovetropics.org)")
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .GET()
                .build();
        return HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApplyAsync(response -> {
                    JsonElement json = JsonParser.parseString(response.body());
                    return CODEC.parse(JsonOps.INSTANCE, json).resultOrPartial(Util.prefix("Failed to parse stream schedule: ", LOGGER::error));
                }, Util.backgroundExecutor())
                .exceptionally(throwable -> {
                    LOGGER.error("Failed to fetch stream schedule", throwable);
                    return Optional.empty();
                });
    }

    @Nullable
    public Entry currentAt(Instant time) {
        for (Entry entry : entries) {
            if (!entry.startTime().isAfter(time) && !time.isAfter(entry.endTime())) {
                return entry;
            }
        }
        return null;
    }

    @Nullable
    public Entry nextAfter(Instant time) {
        for (Entry entry : entries) {
            if (entry.startTime().isAfter(time)) {
                return entry;
            }
        }
        return null;
    }

    public record Entry(String description, Optional<URI> imageUrl, Instant startTime, Instant endTime, List<Host> hosts) {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.STRING.fieldOf("desc").forGetter(Entry::description),
                // No URL is an empty string
                ExtraCodecs.UNTRUSTED_URI.lenientOptionalFieldOf("image_url").forGetter(Entry::imageUrl),
                ExtraCodecs.INSTANT_ISO8601.fieldOf("start_time").forGetter(Entry::startTime),
                ExtraCodecs.INSTANT_ISO8601.fieldOf("end_time").forGetter(Entry::endTime),
                Host.CODEC.listOf().fieldOf("hosts").forGetter(Entry::hosts)
        ).apply(i, Entry::new));
    }

    public record Host(String name) {
        public static final Codec<Host> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.STRING.fieldOf("name").forGetter(Host::name)
        ).apply(i, Host::new));
    }
}
