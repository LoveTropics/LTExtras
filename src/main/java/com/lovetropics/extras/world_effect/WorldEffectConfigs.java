package com.lovetropics.extras.world_effect;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.lovetropics.extras.LTExtras;
import com.lovetropics.lib.codec.CodecRegistry;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.Util;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = LTExtras.MODID)
public class WorldEffectConfigs {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final CodecRegistry<ResourceLocation, WorldEffectHolder> REGISTRY = CodecRegistry.resourceLocationKeys();
    private static final FileToIdConverter LISTER = FileToIdConverter.json("world_effects");

    @SubscribeEvent
    public static void addReloadListener(final AddReloadListenerEvent event) {
        event.addListener((stage, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor) ->
                CompletableFuture.supplyAsync(() -> listEffects(resourceManager, backgroundExecutor), backgroundExecutor).thenCompose(f -> f)
                        .thenCompose(stage::wait)
                        .thenAcceptAsync(effects -> {
                            REGISTRY.clear();
                            effects.forEach((id, effect) ->
                                    REGISTRY.register(id, new WorldEffectHolder(id, effect))
                            );
                            final MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
                            if (server != null) {
                                WorldEffectManager.reload(server);
                            }
                        }, gameExecutor)
        );
    }

    private static CompletableFuture<Map<ResourceLocation, WorldEffect>> listEffects(final ResourceManager resourceManager, final Executor executor) {
        final List<CompletableFuture<Pair<ResourceLocation, WorldEffect>>> futures = LISTER.listMatchingResources(resourceManager).entrySet().stream()
                .map(entry -> {
                    final ResourceLocation path = entry.getKey();
                    final ResourceLocation id = LISTER.fileToId(path);
                    final Resource resource = entry.getValue();
                    return CompletableFuture.supplyAsync(() -> Pair.of(id, loadEffect(JsonOps.INSTANCE, path, resource)), executor);
                })
                .toList();
        return Util.sequence(futures).thenApply(configs -> configs.stream().filter(pair -> pair.getSecond() != null).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
    }

    @Nullable
    private static WorldEffect loadEffect(final DynamicOps<JsonElement> ops, final ResourceLocation path, final Resource resource) {
        try {
            final DataResult<WorldEffect> result;
            try (final BufferedReader reader = resource.openAsReader()) {
                final JsonElement json = JsonParser.parseReader(reader);
                result = WorldEffect.CODEC.parse(ops, json);
            }
            if (result.error().isPresent()) {
                LOGGER.error("Failed to load world effect at {}: {}", path, result.error().get());
            }
            return result.result().orElse(null);
        } catch (final IOException e) {
            LOGGER.error("Failed to load world effect at {}", path, e);
            return null;
        }
    }
}
