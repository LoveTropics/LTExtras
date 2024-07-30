package com.lovetropics.extras.collectible;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;
import java.util.UUID;

public record CollectibleMarker(
        Optional<Holder<Collectible>> collectible,
        Optional<UUID> ownerId
) {
    public static final Codec<CollectibleMarker> CODEC = RecordCodecBuilder.create(i -> i.group(
            Collectible.CODEC.optionalFieldOf("id").forGetter(CollectibleMarker::collectible),
            UUIDUtil.CODEC.optionalFieldOf("owner").forGetter(CollectibleMarker::ownerId)
    ).apply(i, CollectibleMarker::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CollectibleMarker> STREAM_CODEC = StreamCodec.composite(
            Collectible.STREAM_CODEC.apply(ByteBufCodecs::optional), CollectibleMarker::collectible,
            UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs::optional), CollectibleMarker::ownerId,
            CollectibleMarker::new
    );
}
