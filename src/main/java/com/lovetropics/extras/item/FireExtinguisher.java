package com.lovetropics.extras.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record FireExtinguisher(
    float impulseOnHit,
    float impulseOnMiss,
    int durabilityLossPerTick,
    boolean shouldExtinguish,
    float shootDist
) {
    public static final Codec<FireExtinguisher> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.FLOAT.fieldOf("impulse_on_hit").forGetter(FireExtinguisher::impulseOnHit),
            Codec.FLOAT.fieldOf("impulse_on_miss").forGetter(FireExtinguisher::impulseOnMiss),
            Codec.INT.fieldOf("durability_loss_per_tick").forGetter(FireExtinguisher::durabilityLossPerTick),
            Codec.BOOL.fieldOf("should_extinguish").forGetter(FireExtinguisher::shouldExtinguish),
            Codec.FLOAT.fieldOf("shoot_dist").forGetter(FireExtinguisher::shootDist)
    ).apply(i, FireExtinguisher::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FireExtinguisher> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.FLOAT, FireExtinguisher::impulseOnHit,
        ByteBufCodecs.FLOAT, FireExtinguisher::impulseOnMiss,
        ByteBufCodecs.INT, FireExtinguisher::durabilityLossPerTick,
        ByteBufCodecs.BOOL, FireExtinguisher::shouldExtinguish,
        ByteBufCodecs.FLOAT, FireExtinguisher::shootDist,
        FireExtinguisher::new
    );

    public static FireExtinguisher getDefault() {
        return new FireExtinguisher(
            FireExtinguisherItem.DEFAULT_IMPULSE_ON_HIT,
            FireExtinguisherItem.DEFAULT_IMPULSE_ON_MISS,
            1,
            true,
            FireExtinguisherItem.DEFAULT_SHOOT_DIST
        );
    }
}
