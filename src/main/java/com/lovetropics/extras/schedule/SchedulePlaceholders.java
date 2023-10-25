package com.lovetropics.extras.schedule;

import com.lovetropics.extras.LTExtras;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = LTExtras.MODID)
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

    private static void registerForEntry(final boolean next) {
        final String prefix = next ? "next" : "current";
        registerPlaceholder(prefix + "/short_description", next, (ctx, entry) -> PlaceholderResult.value(entry.shortDescription()));
        registerPlaceholder(prefix + "/long_description", next, (ctx, entry) -> PlaceholderResult.value(entry.longDescription()));
        registerPlaceholder(prefix + "/hosts", next, (ctx, entry) -> formatHosts(entry));
        registerPlaceholder(prefix + "/start", next, (ctx, entry) -> formatLocalTime(ctx, entry.startTime()));
        registerPlaceholder(prefix + "/end", next, (ctx, entry) -> formatLocalTime(ctx, entry.endTime()));
        registerPlaceholder(prefix + "/time_until", next, (ctx, entry) -> formatTimeUntil(entry));
    }

    private static void registerPlaceholder(final String id, final boolean next, final PlaceholderFunction function) {
        Placeholders.register(new ResourceLocation(LTExtras.MODID, "schedule/" + id), (ctx, arg) -> {
            final StreamSchedule schedule = SchedulePlaceholders.schedule;
            if (schedule == null) {
                return UNKNOWN;
            }
            final Instant time = Instant.now();
            final StreamSchedule.Entry entry = next ? schedule.nextAfter(time) : schedule.currentAt(time);
            if (entry != null) {
                return function.get(ctx, entry);
            }
            return UNKNOWN;
        });
    }

    private static PlaceholderResult formatHosts(final StreamSchedule.Entry entry) {
        return PlaceholderResult.value(entry.hosts().stream()
                .map(StreamSchedule.Host::name)
                .collect(Collectors.joining(" + "))
        );
    }

    private static PlaceholderResult formatTimeUntil(final StreamSchedule.Entry entry) {
        Duration duration = Duration.between(Instant.now(), entry.startTime());
        if (duration.isNegative()) {
            duration = Duration.ZERO;
        }
        return PlaceholderResult.value(duration.toMinutes() + " minutes");
    }

    private static PlaceholderResult formatLocalTime(final PlaceholderContext ctx, final Instant time) {
        final LocalDateTime localTime = time.atZone(getTimeZone(ctx)).toLocalDateTime();
        return PlaceholderResult.value(TIME_FORMATTER.format(localTime));
    }

    private static ZoneId getTimeZone(final PlaceholderContext ctx) {
        final ServerPlayer player = ctx.player();
        return player != null ? PlayerTimeZone.get(player) : ZoneOffset.UTC;
    }

    @SubscribeEvent
    public static void onServerTick(final TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            return;
        }

        if (!fetchFuture.isDone()) {
            return;
        }

        final Instant time = Instant.now();
        if (Duration.between(lastFetchTime, time).compareTo(FETCH_INTERVAL) > 0) {
            fetchFuture = StreamSchedule.fetch().thenAccept(opt -> opt.ifPresent(s -> schedule = s));
            lastFetchTime = time;
        }
    }

    private interface PlaceholderFunction {
        PlaceholderResult get(PlaceholderContext ctx, StreamSchedule.Entry entry);
    }
}
