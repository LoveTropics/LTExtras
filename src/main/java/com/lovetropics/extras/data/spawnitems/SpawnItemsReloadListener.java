package com.lovetropics.extras.data.spawnitems;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.lovetropics.extras.LTExtras;
import net.minecraft.core.Registry;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;

import java.util.Map;

@EventBusSubscriber
public class SpawnItemsReloadListener extends SimpleJsonResourceReloadListener<SpawnItems> {
    private static final ResourceKey<Registry<SpawnItems>> REGISTRY_KEY = ResourceKey.createRegistryKey(LTExtras.id("spawn_items"));

    public static final BiMap<Identifier, SpawnItems> REGISTRY = Maps.synchronizedBiMap(HashBiMap.create());

    public SpawnItemsReloadListener() {
        super(SpawnItems.CODEC, FileToIdConverter.registry(REGISTRY_KEY));
    }

    @SubscribeEvent
    static void onAddReloadListeners(AddServerReloadListenersEvent event) {
        event.addListener(LTExtras.id("spawn_items"), new SpawnItemsReloadListener());
    }

    @Override
    protected void apply(Map<Identifier, SpawnItems> spawnItems, ResourceManager resourceManager, ProfilerFiller profiler) {
        REGISTRY.clear();
        REGISTRY.putAll(spawnItems);
    }
}
