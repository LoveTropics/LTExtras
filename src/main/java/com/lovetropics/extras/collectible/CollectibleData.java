package com.lovetropics.extras.collectible;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;

import java.util.List;

public record CollectibleData(List<Holder<Collectible>> collectibles, boolean hasUnseen) {
    public static final Codec<CollectibleData> CODEC = RecordCodecBuilder.create(i -> i.group(
            Collectible.CODEC.listOf().fieldOf("collectibles").forGetter(CollectibleData::collectibles),
            Codec.BOOL.optionalFieldOf("has_unseen", false).forGetter(CollectibleData::hasUnseen)
    ).apply(i, CollectibleData::new));
}
