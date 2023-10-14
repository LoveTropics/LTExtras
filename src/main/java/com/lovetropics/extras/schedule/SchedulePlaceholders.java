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
import java.util.function.BiFunction;
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
        registerPlaceholder("current/title", (ctx, current, next) -> PlaceholderResult.value(current.shortDescription()));
        registerPlaceholder("current/description", (ctx, current, next) -> PlaceholderResult.value(current.longDescription()));
        registerPlaceholder("current/hosts", (ctx, current, next) -> formatHosts(current));
        registerPlaceholder("current/start", (ctx, current, next) -> formatLocalTime(ctx, current.time()));
        registerPlaceholder("current/end", (ctx, current, next) -> next != null ? formatLocalTime(ctx, next.time()) : UNKNOWN);

        registerPlaceholderNext("next/title", (ctx, next) -> PlaceholderResult.value(next.shortDescription()));
        registerPlaceholderNext("next/description", (ctx, next) -> PlaceholderResult.value(next.longDescription()));
        registerPlaceholderNext("next/hosts", (ctx, next) -> formatHosts(next));
        registerPlaceholderNext("next/start", (ctx, next) -> formatLocalTime(ctx, next.time()));
        registerPlaceholderNext("next/time_until", (ctx, next) -> formatTimeUntil(next));
    }

    private static void registerPlaceholder(final String id, final PlaceholderFunction function) {
        Placeholders.register(new ResourceLocation(LTExtras.MODID, "schedule/" + id), (ctx, arg) -> {
            final StreamSchedule schedule = SchedulePlaceholders.schedule;
            if (schedule == null) {
                return UNKNOWN;
            }
            final StreamSchedule.State state = schedule.stateAt(Instant.now());
            if (state != null) {
                return function.get(ctx, state.currentEntry(), state.nextEntry());
            }
            return UNKNOWN;
        });
    }

    private static void registerPlaceholderNext(final String id, final BiFunction<PlaceholderContext, StreamSchedule.Entry, PlaceholderResult> function) {
        registerPlaceholder(id, (ctx, current, next) -> next != null ? function.apply(ctx, next) : UNKNOWN);
    }

    private static PlaceholderResult formatHosts(final StreamSchedule.Entry entry) {
        return PlaceholderResult.value(entry.hosts().stream()
                .map(StreamSchedule.Host::name)
                .collect(Collectors.joining(", "))
        );
    }

    private static PlaceholderResult formatTimeUntil(final StreamSchedule.Entry entry) {
        Duration duration = Duration.between(Instant.now(), entry.time());
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
        PlaceholderResult get(PlaceholderContext ctx, StreamSchedule.Entry current, @Nullable StreamSchedule.Entry next);
    }
}
