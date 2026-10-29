package com.lovetropics.extras.item.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

/**
 * LTExtras
 * FIRE EMOJI, FIRE EMOJI, FIRE EMOJI
 */
public record ArrowItemProjectileOverrideData(
        Optional<Double> fixedDamage,
        Optional<Double> gravity
) {
    public static final Codec<ArrowItemProjectileOverrideData> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.DOUBLE.optionalFieldOf("fixed_damage").forGetter(ArrowItemProjectileOverrideData::fixedDamage),
            Codec.DOUBLE.optionalFieldOf("gravity").forGetter(ArrowItemProjectileOverrideData::gravity)
    ).apply(builder, ArrowItemProjectileOverrideData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ArrowItemProjectileOverrideData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(ByteBufCodecs.DOUBLE), ArrowItemProjectileOverrideData::fixedDamage,
            ByteBufCodecs.optional(ByteBufCodecs.DOUBLE), ArrowItemProjectileOverrideData::gravity,
            ArrowItemProjectileOverrideData::new
    );
}
