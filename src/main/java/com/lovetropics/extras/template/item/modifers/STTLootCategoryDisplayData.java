package com.lovetropics.extras.template.item.modifers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;


/**
 * LTExtras
 * FIRE EMOJI, FIRE EMOJI, FIRE EMOJI
 */
public record STTLootCategoryDisplayData(
        String name,
        int color
) {
    public static final Codec<STTLootCategoryDisplayData> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.STRING.fieldOf("name").forGetter(STTLootCategoryDisplayData::name),
            Codec.INT.fieldOf("color").forGetter(STTLootCategoryDisplayData::color)
    ).apply(builder, STTLootCategoryDisplayData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, STTLootCategoryDisplayData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, STTLootCategoryDisplayData::name,
            ByteBufCodecs.INT, STTLootCategoryDisplayData::color,
            STTLootCategoryDisplayData::new
    );
}
