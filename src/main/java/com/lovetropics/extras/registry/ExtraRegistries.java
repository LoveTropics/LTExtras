package com.lovetropics.extras.registry;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.collectible.Collectible;
import com.lovetropics.extras.data.poi.MapConfig;
import com.lovetropics.extras.data.poi.PoiConfig;
import com.lovetropics.extras.item.sensor.PlayerSensor;
import com.lovetropics.extras.mounts.Mount;
import com.lovetropics.extras.model_modifer.ModelModifier;
import com.lovetropics.extras.model_modifer.ModelModifierType;
import com.lovetropics.extras.model_modifer.ExtraModelModifiers;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

@EventBusSubscriber
public class ExtraRegistries {
    public static final ResourceKey<Registry<Collectible>> COLLECTIBLE = createKey("collectible");
    public static final ResourceKey<Registry<MapConfig>> MAP = createKey("map");
    public static final ResourceKey<Registry<ModelModifier>> MODEL_MODIFIER = createKey("model_modifier");
    public static final ResourceKey<Registry<ModelModifierType<?>>> MODIFIER_TYPE_KEY = createKey("modifier_type");
    public static final ResourceKey<Registry<PlayerSensor>> PLAYER_SENSOR = createKey("player_sensor");

    public static final Registry<ModelModifierType<?>> MODIFIER_TYPES = new RegistryBuilder<>(MODIFIER_TYPE_KEY)
            .sync(true)
            .create();

    // Fake registries
    public static final ResourceKey<Registry<EnvironmentAttributeMap>> ENVIRONMENT_ATTRIBUTE_BUNDLE = createKey("environment_attribute_bundle");
    public static final ResourceKey<Registry<PoiConfig>> POI = createKey("poi");
    public static final ResourceKey<Registry<Mount>> MOUNT = createKey("mount");

    @SubscribeEvent
    public static void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(COLLECTIBLE, Collectible.DIRECT_CODEC, Collectible.DIRECT_CODEC);
        event.dataPackRegistry(MAP, MapConfig.DIRECT_CODEC, MapConfig.DIRECT_CODEC);
        event.dataPackRegistry(MODEL_MODIFIER, ExtraModelModifiers.CODEC, ExtraModelModifiers.CODEC);
        event.dataPackRegistry(PLAYER_SENSOR, PlayerSensor.DIRECT_CODEC, PlayerSensor.DIRECT_CODEC);
    }

    @SubscribeEvent
    public static void registerRegistries(NewRegistryEvent event) {
        event.register(MODIFIER_TYPES);
    }

    private static <T> ResourceKey<Registry<T>> createKey(String name) {
        return ResourceKey.createRegistryKey(LTExtras.id(name));
    }
}
