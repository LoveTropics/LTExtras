package com.lovetropics.extras.data.poi;

import com.lovetropics.extras.registry.ExtraRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public record MapConfig(
        Component description,
        ResourceKey<Level> dimension,
        int minX,
        int minZ,
        int maxX,
        int maxZ,
        ResourceLocation texture
) {
    public static final Codec<MapConfig> DIRECT_CODEC = RecordCodecBuilder.create(i -> i.group(
            ComponentSerialization.CODEC.fieldOf("description").forGetter(MapConfig::description),
            Level.RESOURCE_KEY_CODEC.fieldOf("dimension").forGetter(MapConfig::dimension),
            Codec.INT.fieldOf("min_x").forGetter(MapConfig::minX),
            Codec.INT.fieldOf("min_z").forGetter(MapConfig::minZ),
            Codec.INT.fieldOf("max_x").forGetter(MapConfig::maxX),
            Codec.INT.fieldOf("max_z").forGetter(MapConfig::maxZ),
            ResourceLocation.CODEC.fieldOf("texture").forGetter(MapConfig::texture)
    ).apply(i, MapConfig::new));

    public static final Codec<Holder<MapConfig>> CODEC = RegistryFixedCodec.create(ExtraRegistries.MAP);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<MapConfig>> STREAM_CODEC = ByteBufCodecs.holderRegistry(ExtraRegistries.MAP);

    public int markerX(BlockPos pos) {
        int width = maxX - minX;
        return (pos.getX() - minX) * MapManager.MAP_SIZE / width;
    }

    public int markerY(BlockPos pos) {
        int height = maxZ - minZ;
        return (pos.getZ() - minZ) * MapManager.MAP_SIZE / height;
    }
}
