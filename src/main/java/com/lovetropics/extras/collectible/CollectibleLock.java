package com.lovetropics.extras.collectible;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record CollectibleLock(
        List<Component> tooltip
) {
    public static final Codec<CollectibleLock> CODEC = RecordCodecBuilder.create(i -> i.group(
            ComponentSerialization.CODEC.listOf(0, 16).fieldOf("tooltip").forGetter(CollectibleLock::tooltip)
    ).apply(i, CollectibleLock::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CollectibleLock> STREAM_CODEC = StreamCodec.composite(
            ComponentSerialization.STREAM_CODEC.apply(ByteBufCodecs.list(16)), CollectibleLock::tooltip,
            CollectibleLock::new
    );
}
