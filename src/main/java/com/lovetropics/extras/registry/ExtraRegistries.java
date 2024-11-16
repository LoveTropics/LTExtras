package com.lovetropics.extras.registry;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.collectible.Collectible;
import com.lovetropics.extras.data.poi.MapConfig;
import com.lovetropics.extras.data.poi.PoiConfig;
import com.lovetropics.extras.world_effect.WorldEffect;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

@EventBusSubscriber(modid = LTExtras.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ExtraRegistries {
    public static final ResourceKey<Registry<Collectible>> COLLECTIBLE = createKey("collectible");
    public static final ResourceKey<Registry<MapConfig>> MAP = createKey("map");

    // Fake registries
    public static final ResourceKey<Registry<WorldEffect>> WORLD_EFFECT = createKey("world_effect");
    public static final ResourceKey<Registry<PoiConfig>> POI = createKey("poi");

    @SubscribeEvent
    public static void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(COLLECTIBLE, Collectible.DIRECT_CODEC, Collectible.DIRECT_CODEC);
        event.dataPackRegistry(MAP, MapConfig.DIRECT_CODEC, MapConfig.DIRECT_CODEC);
    }

    private static <T> ResourceKey<Registry<T>> createKey(String name) {
        return ResourceKey.createRegistryKey(LTExtras.location(name));
    }
}
