package com.lovetropics.extras.schedule;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.lovetropics.extras.ExtrasConfig;
import com.lovetropics.lib.codec.MoreCodecs;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import org.apache.http.HttpHeaders;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.*;
import java.time.format.DateTimeFormatter;
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
        final String authKey = ExtrasConfig.TECH_STACK.authKey.get();
        if (authKey.isEmpty()) {
            return CompletableFuture.completedFuture(Optional.empty());
        }
        final HttpRequest request = HttpRequest.newBuilder(URI.create(ExtrasConfig.TECH_STACK.scheduleUrl.get()))
                .header("Authorization", "Bearer " + authKey)
                .header(HttpHeaders.USER_AGENT, "LTExtras 1.0 (lovetropics.org)")
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .GET()
                .build();
        return HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApplyAsync(response -> {
                    final JsonElement json = JsonParser.parseString(response.body());
                    return CODEC.parse(JsonOps.INSTANCE, json).resultOrPartial(Util.prefix("Failed to parse stream schedule: ", LOGGER::error));
                }, Util.backgroundExecutor())
                .exceptionally(throwable -> {
                    LOGGER.error("Failed to fetch stream schedule", throwable);
                    return Optional.empty();
                });
    }

    @Nullable
    public State stateAt(final Instant time) {
        for (int i = 0; i < entries.size(); i++) {
            final Entry entry = entries.get(i);
            if (!entry.time().isBefore(time)) {
                final Entry nextEntry = i + 1 < entries.size() ? entries.get(i + 1) : null;
                return new State(entry, nextEntry);
            }
        }
        return null;
    }

    public record Entry(String shortDescription, String longDescription, Instant time, List<Host> hosts) {
        private static final Codec<Instant> TIME_CODEC = MoreCodecs.localDateTime(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")).xmap(
                localTime -> localTime.atOffset(ZoneOffset.UTC).toInstant(),
                instant -> instant.atOffset(ZoneOffset.UTC).toLocalDateTime()
        );

        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.STRING.fieldOf("short_desc").forGetter(Entry::shortDescription),
                Codec.STRING.optionalFieldOf("long_desc", "").forGetter(Entry::longDescription),
                TIME_CODEC.fieldOf("time").forGetter(Entry::time),
                Host.CODEC.listOf().fieldOf("hosts").forGetter(Entry::hosts)
        ).apply(i, Entry::new));
    }

    public record Host(String name) {
        public static final Codec<Host> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.STRING.fieldOf("name").forGetter(Host::name)
        ).apply(i, Host::new));
    }

    public record State(Entry currentEntry, @Nullable Entry nextEntry) {
    }
}
