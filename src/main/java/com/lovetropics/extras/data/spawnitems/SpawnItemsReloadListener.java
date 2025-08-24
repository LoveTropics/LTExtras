package com.lovetropics.extras.data.spawnitems;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.lovetropics.extras.LTExtras;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;

import java.util.Map;

@EventBusSubscriber(modid = LTExtras.MODID)
public class SpawnItemsReloadListener extends SimpleJsonResourceReloadListener<SpawnItems> {
    // TODO: Move it to ltextras namespace
    private static final ResourceKey<Registry<SpawnItems>> REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.withDefaultNamespace("spawn_items"));

    public static final BiMap<ResourceLocation, SpawnItems> REGISTRY = Maps.synchronizedBiMap(HashBiMap.create());

    public SpawnItemsReloadListener(HolderLookup.Provider registries) {
        super(registries, SpawnItems.CODEC, REGISTRY_KEY);
    }

    @SubscribeEvent
    static void onAddReloadListeners(AddServerReloadListenersEvent event) {
        event.addListener(LTExtras.location("spawn_items"), new SpawnItemsReloadListener(event.getRegistryAccess()));
    }

    @Override
    protected void apply(Map<ResourceLocation, SpawnItems> spawnItems, ResourceManager resourceManager, ProfilerFiller profiler) {
        REGISTRY.clear();
        REGISTRY.putAll(spawnItems);
    }
}
