package com.lovetropics.extras.world_effect;

import com.lovetropics.extras.data.Named;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
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

@EventBusSubscriber
public class WorldEffectManager {
    private static final Map<ResourceKey<Level>, Map<Identifier, Entry>> EFFECTS_BY_DIMENSION = new Reference2ObjectOpenHashMap<>();

    public static void apply(ServerLevel level, Named<WorldEffect> effect, long expiresAt) {
        Map<Identifier, Entry> effects = EFFECTS_BY_DIMENSION.computeIfAbsent(level.dimension(), k -> new Object2ObjectOpenHashMap<>());
        Entry entry = new Entry(effect.value(), expiresAt);
        if (effects.put(effect.id(), entry) == null) {
            apply(level, effect.value(), false);
        }
    }

    public static void clear(ServerLevel level, Identifier effectId) {
        Map<Identifier, Entry> effects = EFFECTS_BY_DIMENSION.get(level.dimension());
        if (effects == null) {
            return;
        }
        Entry entry = effects.remove(effectId);
        if (entry != null) {
            clear(level, entry.effect(), false);
        }
    }

    private static void apply(ServerLevel level, WorldEffect effect, boolean immediate) {
        for (ServerPlayer player : level.players()) {
            effect.apply(player, immediate);
        }
    }

    private static void clear(ServerLevel level, WorldEffect effect, boolean immediate) {
        for (ServerPlayer player : level.players()) {
            effect.clear(player, immediate);
        }
    }

    public static void clearAll(ServerLevel level) {
        Map<Identifier, Entry> effects = EFFECTS_BY_DIMENSION.remove(level.dimension());
        if (effects == null) {
            return;
        }
        for (Entry entry : effects.values()) {
            clear(level, entry.effect(), false);
        }
    }

    public static void reload(MinecraftServer server) {
        EFFECTS_BY_DIMENSION.forEach((dimension, effects) -> {
            ServerLevel level = server.getLevel(dimension);
            if (level != null) {
                Map<Identifier, Entry> newEffects = reloadInDimension(effects, level);
                effects.clear();
                effects.putAll(newEffects);
            }
        });
    }

    private static Map<Identifier, Entry> reloadInDimension(Map<Identifier, Entry> effects, ServerLevel level) {
        for (Entry entry : effects.values()) {
            clear(level, entry.effect(), true);
        }
        Map<Identifier, Entry> newEffects = effects.entrySet().stream()
                .map(entry -> {
                    Named<WorldEffect> newEffect = WorldEffectConfigs.REGISTRY.get(entry.getKey());
                    if (newEffect != null) {
                        return Pair.of(entry.getKey(), entry.getValue().rebind(newEffect.value()));
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
        for (Entry entry : newEffects.values()) {
            apply(level, entry.effect(), true);
        }
        return newEffects;
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        MinecraftServer server = event.getServer();
        long gameTime = server.overworld().getGameTime();
        EFFECTS_BY_DIMENSION.entrySet().removeIf(entry -> {
            ServerLevel level = server.getLevel(entry.getKey());
            return level == null || tickDimension(entry.getValue().values(), gameTime, level);
        });
    }

    private static boolean tickDimension(Collection<Entry> entries, long gameTime, ServerLevel level) {
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
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            forEachEffect(player, player.level(), (e, p) -> e.apply(p, true));
        }
    }

    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            MinecraftServer server = player.level().getServer();
            ServerLevel fromLevel = server.getLevel(event.getFrom());
            if (fromLevel != null) {
                forEachEffect(player, fromLevel, (e, p) -> e.clear(p, true));
            }
            ServerLevel toLevel = server.getLevel(event.getTo());
            if (toLevel != null) {
                forEachEffect(player, toLevel, (e, p) -> e.apply(p, true));
            }
        }
    }

    private static void forEachEffect(ServerPlayer player, ServerLevel level, BiConsumer<WorldEffect, ServerPlayer> consumer) {
        Map<Identifier, Entry> effects = EFFECTS_BY_DIMENSION.get(level.dimension());
        if (effects != null && !effects.isEmpty()) {
            for (Entry entry : effects.values()) {
                consumer.accept(entry.effect(), player);
            }
        }
    }

    private record Entry(WorldEffect effect, long expiresAt) {
        public Entry rebind(WorldEffect effect) {
            return new Entry(effect, expiresAt);
        }
    }
}
