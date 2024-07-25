package com.lovetropics.extras.data.spawnitems;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.lovetropics.extras.LTExtras;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

import java.util.Map;

@EventBusSubscriber(modid = LTExtras.MODID)
public class SpawnItemsReloadListener extends SimpleJsonResourceReloadListener {
    public static final BiMap<ResourceLocation, SpawnItems> REGISTRY = Maps.synchronizedBiMap(HashBiMap.create());

    private final HolderLookup.Provider registries;

    public SpawnItemsReloadListener(Gson gson, String directory, HolderLookup.Provider registries) {
        super(gson, directory);
        this.registries = registries;
    }

    @SubscribeEvent
    static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new SpawnItemsReloadListener(new GsonBuilder().setLenient().create(), "spawn_items", event.getRegistryAccess()));
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsons, ResourceManager resources, ProfilerFiller profiler) {
        RegistryOps<JsonElement> ops = registries.createSerializationContext(JsonOps.INSTANCE);
        profiler.push("lt:spawn_items");
        REGISTRY.clear();
        jsons.forEach((id, json) ->
                REGISTRY.put(id, SpawnItems.CODEC.parse(ops, json).getOrThrow(JsonParseException::new))
        );
        profiler.pop();
    }
}
