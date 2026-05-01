package com.lovetropics.extras.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

public record SimpleDataPackLister<T>(String root, ResourceKey<Registry<T>> registryKey, Codec<T> codec) {
    private static final Logger LOGGER = LogUtils.getLogger();

    public FileToIdConverter fileToIdConverter() {
        return FileToIdConverter.json(root);
    }

    public CompletableFuture<List<Named<T>>> load(RegistryAccess registryAccess, ResourceManager resourceManager, Executor executor) {
        RegistryOps<JsonElement> ops = registryAccess.createSerializationContext(JsonOps.INSTANCE);
        return CompletableFuture.supplyAsync(() -> listEntries(ops, resourceManager, executor), executor).thenCompose(Function.identity());
    }

    private CompletableFuture<List<Named<T>>> listEntries(DynamicOps<JsonElement> ops, ResourceManager resourceManager, Executor executor) {
        FileToIdConverter lister = fileToIdConverter();
        List<CompletableFuture<Named<T>>> futures = lister.listMatchingResources(resourceManager).entrySet().stream()
                .map(resource -> {
                    Identifier path = resource.getKey();
                    Identifier id = lister.fileToId(path);
                    return CompletableFuture.supplyAsync(() -> {
                        T entry = loadEntry(ops, path, resource.getValue());
                        return entry != null ? new Named<>(ResourceKey.create(registryKey, id), entry) : null;
                    }, executor);
                })
                .toList();
        return Util.sequence(futures).thenApply(configs -> configs.stream().filter(Objects::nonNull).toList());
    }

    @Nullable
    private T loadEntry(DynamicOps<JsonElement> ops, Identifier path, Resource resource) {
        try (BufferedReader reader = resource.openAsReader()) {
            return codec.parse(ops, JsonParser.parseReader(reader))
                    .ifError(error -> LOGGER.error("Failed to load data pack entry at {}: {}", path, error.error()))
                    .resultOrPartial()
                    .orElse(null);
        } catch (IOException | JsonParseException e) {
            LOGGER.error("Failed to load data pack entry at {}", path, e);
            return null;
        }
    }
}
