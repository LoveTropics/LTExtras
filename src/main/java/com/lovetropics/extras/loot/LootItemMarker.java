package com.lovetropics.extras.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;
import java.util.UUID;

/**
 * LTExtras
 * FIRE EMOJI, FIRE EMOJI, FIRE EMOJI
 */
public record LootItemMarker(
        Optional<Holder<LootItem>> lootItem
) {
    public static final Codec<LootItemMarker> CODEC = RecordCodecBuilder.create(i -> i.group(
            LootItem.CODEC.optionalFieldOf("id").forGetter(LootItemMarker::lootItem)
    ).apply(i, LootItemMarker::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, LootItemMarker> STREAM_CODEC = StreamCodec.composite(
            LootItem.STREAM_CODEC.apply(ByteBufCodecs::optional), LootItemMarker::lootItem,
            LootItemMarker::new
    );
}
