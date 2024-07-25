package com.lovetropics.extras.schedule;

import com.lovetropics.extras.LTExtras;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import javax.annotation.Nullable;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@EventBusSubscriber(modid = LTExtras.MODID)
public class SchedulePlaceholders {
    private static final PlaceholderResult UNKNOWN = PlaceholderResult.value("?");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("EEE HH:mm");

    private static final Duration FETCH_INTERVAL = Duration.ofMinutes(5);

    private static CompletableFuture<?> fetchFuture = CompletableFuture.completedFuture(null);
    @Nullable
    private static StreamSchedule schedule;
    private static Instant lastFetchTime = Instant.EPOCH;

    static {
        registerForEntry(false);
        registerForEntry(true);
    }

    private static void registerForEntry(boolean next) {
        String prefix = next ? "next" : "current";
        registerPlaceholder(prefix + "/short_description", next, (ctx, entry) -> PlaceholderResult.value(entry.shortDescription()));
        registerPlaceholder(prefix + "/long_description", next, (ctx, entry) -> PlaceholderResult.value(entry.longDescription()));
        registerPlaceholder(prefix + "/hosts", next, (ctx, entry) -> formatHosts(entry));
        registerPlaceholder(prefix + "/start", next, (ctx, entry) -> formatLocalTime(ctx, entry.startTime()));
        registerPlaceholder(prefix + "/end", next, (ctx, entry) -> formatLocalTime(ctx, entry.endTime()));
        registerPlaceholder(prefix + "/time_until", next, (ctx, entry) -> formatTimeUntil(entry));
    }

    private static void registerPlaceholder(String id, boolean next, PlaceholderFunction function) {
        Placeholders.register(LTExtras.location("schedule/" + id), (ctx, arg) -> {
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
        });
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
        ServerPlayer player = ctx.player();
        return player != null ? PlayerTimeZone.get(player) : ZoneOffset.UTC;
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
