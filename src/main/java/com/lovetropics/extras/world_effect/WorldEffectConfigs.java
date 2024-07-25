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
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@EventBusSubscriber(modid = LTExtras.MODID)
public class WorldEffectConfigs {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final CodecRegistry<ResourceLocation, WorldEffectHolder> REGISTRY = CodecRegistry.resourceLocationKeys();
    private static final FileToIdConverter LISTER = FileToIdConverter.json("world_effects");

    @SubscribeEvent
    public static void addReloadListener(AddReloadListenerEvent event) {
        RegistryAccess registries = event.getRegistryAccess();
        RegistryOps<JsonElement> ops = registries.createSerializationContext(JsonOps.INSTANCE);
        event.addListener((stage, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor) ->
                CompletableFuture.supplyAsync(() -> listEffects(ops, resourceManager, backgroundExecutor), backgroundExecutor).thenCompose(f -> f)
                        .thenCompose(stage::wait)
                        .thenAcceptAsync(effects -> {
                            REGISTRY.clear();
                            effects.forEach((id, effect) ->
                                    REGISTRY.register(id, new WorldEffectHolder(id, effect))
                            );
                            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
                            if (server != null) {
                                WorldEffectManager.reload(server);
                            }
                        }, gameExecutor)
        );
    }

    private static CompletableFuture<Map<ResourceLocation, WorldEffect>> listEffects(DynamicOps<JsonElement> ops, ResourceManager resourceManager, Executor executor) {
        List<CompletableFuture<Pair<ResourceLocation, WorldEffect>>> futures = LISTER.listMatchingResources(resourceManager).entrySet().stream()
                .map(entry -> {
                    ResourceLocation path = entry.getKey();
                    ResourceLocation id = LISTER.fileToId(path);
                    Resource resource = entry.getValue();
                    return CompletableFuture.supplyAsync(() -> Pair.of(id, loadEffect(ops, path, resource)), executor);
                })
                .toList();
        return Util.sequence(futures).thenApply(configs -> configs.stream().filter(pair -> pair.getSecond() != null).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
    }

    @Nullable
    private static WorldEffect loadEffect(DynamicOps<JsonElement> ops, ResourceLocation path, Resource resource) {
        try {
            DataResult<WorldEffect> result;
            try (BufferedReader reader = resource.openAsReader()) {
                JsonElement json = JsonParser.parseReader(reader);
                result = WorldEffect.CODEC.parse(ops, json);
            }
            if (result.error().isPresent()) {
                LOGGER.error("Failed to load world effect at {}: {}", path, result.error().get());
            }
            return result.result().orElse(null);
        } catch (IOException e) {
            LOGGER.error("Failed to load world effect at {}", path, e);
            return null;
        }
    }
}
