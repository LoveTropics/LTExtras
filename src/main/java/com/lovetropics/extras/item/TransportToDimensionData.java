package com.lovetropics.extras.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import java.util.Optional;

public record TransportToDimensionData(ResourceKey<Level> destinationLevel, Optional<Vec3> destinationPos) {
    public static final Codec<TransportToDimensionData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceKey.codec(Registries.DIMENSION).fieldOf("destination_level").forGetter(TransportToDimensionData::destinationLevel),
            Vec3.CODEC.optionalFieldOf("destination_pos").forGetter(TransportToDimensionData::destinationPos)
    ).apply(instance, TransportToDimensionData::new));

    @Override
    public String toString() {
        return "TransportToDimensionData[" +
                "dimensionType=" + destinationLevel + ", " +
                "returnLocation=" + destinationPos + ']';
    }
}
