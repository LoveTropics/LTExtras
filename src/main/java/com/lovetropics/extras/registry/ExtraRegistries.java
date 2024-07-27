package com.lovetropics.extras.registry;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.collectible.Collectible;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

@EventBusSubscriber(modid = LTExtras.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ExtraRegistries {
    public static final ResourceKey<Registry<Collectible>> COLLECTIBLE = createKey("collectible");

    @SubscribeEvent
    public static void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(COLLECTIBLE, Collectible.DIRECT_CODEC, Collectible.DIRECT_CODEC);
    }

    private static <T> ResourceKey<Registry<T>> createKey(String name) {
        return ResourceKey.createRegistryKey(LTExtras.location(name));
    }
}
