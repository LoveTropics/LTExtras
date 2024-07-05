package com.lovetropics.extras.world_effect;

import com.lovetropics.extras.LTExtras;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@EventBusSubscriber(modid = LTExtras.MODID)
public class WorldEffectManager {
    private static final Map<ResourceKey<Level>, Map<ResourceLocation, Entry>> EFFECTS_BY_DIMENSION = new Reference2ObjectOpenHashMap<>();

    public static void apply(final ServerLevel level, final WorldEffectHolder effect, final long expiresAt) {
        final Map<ResourceLocation, Entry> effects = EFFECTS_BY_DIMENSION.computeIfAbsent(level.dimension(), k -> new Object2ObjectOpenHashMap<>());
        final Entry entry = new Entry(effect.value(), expiresAt);
        if (effects.put(effect.id(), entry) == null) {
            apply(level, effect.value(), false);
        }
    }

    public static void clear(final ServerLevel level, final ResourceLocation effectId) {
        final Map<ResourceLocation, Entry> effects = EFFECTS_BY_DIMENSION.get(level.dimension());
        if (effects == null) {
            return;
        }
        final Entry entry = effects.remove(effectId);
        if (entry != null) {
            clear(level, entry.effect(), false);
        }
    }

    private static void apply(final ServerLevel level, final WorldEffect effect, final boolean immediate) {
        for (final ServerPlayer player : level.players()) {
            effect.apply(player, immediate);
        }
    }

    private static void clear(final ServerLevel level, final WorldEffect effect, final boolean immediate) {
        for (final ServerPlayer player : level.players()) {
            effect.clear(player, immediate);
        }
    }

    public static void reload(final MinecraftServer server) {
        EFFECTS_BY_DIMENSION.forEach((dimension, effects) -> {
            final ServerLevel level = server.getLevel(dimension);
            if (level != null) {
                final Map<ResourceLocation, Entry> newEffects = reloadInDimension(effects, level);
                effects.clear();
                effects.putAll(newEffects);
            }
        });
    }

    private static Map<ResourceLocation, Entry> reloadInDimension(final Map<ResourceLocation, Entry> effects, final ServerLevel level) {
        for (final Entry entry : effects.values()) {
            clear(level, entry.effect(), true);
        }
        final Map<ResourceLocation, Entry> newEffects = effects.entrySet().stream()
                .map(entry -> {
                    final WorldEffectHolder newEffect = WorldEffectConfigs.REGISTRY.get(entry.getKey());
                    if (newEffect != null) {
                        return Pair.of(entry.getKey(), entry.getValue().rebind(newEffect.value()));
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
        for (final Entry entry : newEffects.values()) {
            apply(level, entry.effect(), true);
        }
        return newEffects;
    }

    @SubscribeEvent
    public static void onServerTick(final ServerTickEvent.Post event) {
        final MinecraftServer server = event.getServer();
        final long gameTime = server.overworld().getGameTime();
        EFFECTS_BY_DIMENSION.entrySet().removeIf(entry -> {
            final ServerLevel level = server.getLevel(entry.getKey());
            return level == null || tickDimension(entry.getValue().values(), gameTime, level);
        });
    }

    private static boolean tickDimension(final Collection<Entry> entries, final long gameTime, final ServerLevel level) {
        entries.removeIf(e -> {
            if (gameTime > e.expiresAt()) {
                clear(level, e.effect(), false);
                return true;
            }
            return false;
        });
        return entries.isEmpty();
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(final PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof final ServerPlayer player) {
            forEachEffect(player, player.serverLevel(), (e, p) -> e.apply(p, true));
        }
    }

    @SubscribeEvent
    public static void onPlayerChangeDimension(final PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof final ServerPlayer player) {
            final MinecraftServer server = player.server;
            final ServerLevel fromLevel = server.getLevel(event.getFrom());
            if (fromLevel != null) {
                forEachEffect(player, fromLevel, (e, p) -> e.clear(p, true));
            }
            final ServerLevel toLevel = server.getLevel(event.getTo());
            if (toLevel != null) {
                forEachEffect(player, toLevel, (e, p) -> e.apply(p, true));
            }
        }
    }

    private static void forEachEffect(final ServerPlayer player, final ServerLevel level, final BiConsumer<WorldEffect, ServerPlayer> consumer) {
        final Map<ResourceLocation, Entry> effects = EFFECTS_BY_DIMENSION.get(level.dimension());
        if (effects != null && !effects.isEmpty()) {
            for (final Entry entry : effects.values()) {
                consumer.accept(entry.effect(), player);
            }
        }
    }

    private record Entry(WorldEffect effect, long expiresAt) {
        public Entry rebind(final WorldEffect effect) {
            return new Entry(effect, expiresAt);
        }
    }
}
