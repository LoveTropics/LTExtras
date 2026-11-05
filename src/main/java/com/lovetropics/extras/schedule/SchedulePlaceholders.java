package com.lovetropics.extras.schedule;

import com.lovetropics.extras.LTExtras;
import eu.pb4.placeholders.api.ArgumentParser;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.ServerPlaceholderContext;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import org.jspecify.annotations.Nullable;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@EventBusSubscriber
public class SchedulePlaceholders {
    private static final PlaceholderResult UNKNOWN = PlaceholderResult.value("?");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("EEE HH:mm");

    private static final ArgumentParser<Integer> INT_PARSER = string -> {
        if (string == null) {
            return -1;
        }
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return -1;
        }
    };

    private static final Duration FETCH_INTERVAL = Duration.ofMinutes(5);

    private static CompletableFuture<?> fetchFuture = CompletableFuture.completedFuture(null);
    private static @Nullable StreamSchedule schedule;
    private static Instant lastFetchTime = Instant.EPOCH;

    static {
        registerPlaceholder("description", (_, entry) -> PlaceholderResult.value(entry.description()));
        registerPlaceholder("hosts", (_, entry) -> formatHosts(entry));
        registerPlaceholder("title", (_, entry) -> PlaceholderResult.value(entry.title()));
        registerPlaceholder("host_text", (_, entry) -> PlaceholderResult.value(entry.hostText()));
        registerPlaceholder("start", (ctx, entry) -> formatLocalTime(ctx, entry.startTime()));
        registerPlaceholder("end", (ctx, entry) -> formatLocalTime(ctx, entry.endTime()));
        registerPlaceholder("time_until", (ctx, entry) -> formatTimeUntil(entry));
    }

    private static void registerPlaceholder(String id, PlaceholderFunction function) {
        Placeholders.registerServer(LTExtras.id("schedule/next/" + id), (ctx, arg) -> resolvePlaceholder(true, function, ctx));
        Placeholders.registerServer(LTExtras.id("schedule/current/" + id), (ctx, arg) -> resolvePlaceholder(false, function, ctx));
        Placeholders.registerServer(LTExtras.id("schedule/index/" + id), INT_PARSER, (ctx, index) -> {
            StreamSchedule schedule = SchedulePlaceholders.schedule;
            if (schedule == null) {
                return UNKNOWN;
            }
            StreamSchedule.Entry entry = schedule.atIndex(index);
            if (entry != null) {
                return function.get(ctx, entry);
            }
            return UNKNOWN;
        });
    }

    private static PlaceholderResult resolvePlaceholder(boolean next, PlaceholderFunction function, ServerPlaceholderContext ctx) {
        StreamSchedule schedule = SchedulePlaceholders.schedule;
        if (schedule == null) {
            return UNKNOWN;
        }
        Instant time = Instant.now();
        StreamSchedule.Entry entry = next ? schedule.nextAfter(time) : schedule.currentAt(time);
        if (entry != null) {
            return function.get(ctx, entry);
        }
        return UNKNOWN;
    }

    private static PlaceholderResult formatHosts(StreamSchedule.Entry entry) {
        return PlaceholderResult.value(entry.hosts().stream()
                .map(StreamSchedule.Host::name)
                .collect(Collectors.joining(" + "))
        );
    }

    private static PlaceholderResult formatTimeUntil(StreamSchedule.Entry entry) {
        Duration duration = Duration.between(Instant.now(), entry.startTime());
        if (duration.isNegative()) {
            duration = Duration.ZERO;
        }
        return PlaceholderResult.value(duration.toMinutes() + " minutes");
    }

    private static PlaceholderResult formatLocalTime(PlaceholderContext ctx, Instant time) {
        LocalDateTime localTime = time.atZone(getTimeZone(ctx)).toLocalDateTime();
        return PlaceholderResult.value(TIME_FORMATTER.format(localTime));
    }

    private static ZoneId getTimeZone(PlaceholderContext ctx) {
        return ctx.player() instanceof ServerPlayer player ? PlayerTimeZone.get(player) : ZoneOffset.UTC;
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Pre event) {
        if (!fetchFuture.isDone()) {
            return;
        }

        Instant time = Instant.now();
        if (Duration.between(lastFetchTime, time).compareTo(FETCH_INTERVAL) > 0) {
            fetchFuture = StreamSchedule.fetch().thenAccept(opt -> opt.ifPresent(s -> schedule = s));
            lastFetchTime = time;
        }
    }

    private interface PlaceholderFunction {
        PlaceholderResult get(PlaceholderContext ctx, StreamSchedule.Entry entry);
    }
}
