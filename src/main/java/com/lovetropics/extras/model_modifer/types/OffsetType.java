package com.lovetropics.extras.model_modifer.types;

import com.lovetropics.extras.model_modifer.ExtraModelModifierTypes;
import com.lovetropics.extras.model_modifer.ModelModifier;
import com.lovetropics.extras.model_modifer.ModelModifierType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record OffsetType(float x, float y, float z) implements ModelModifier<OffsetType> {

    private static final Codec<Float> RANGE_FLOAT_CODEC = Codec.floatRange(0, 16);

    public static final MapCodec<OffsetType> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            RANGE_FLOAT_CODEC.fieldOf("x").forGetter(OffsetType::x),
            RANGE_FLOAT_CODEC.fieldOf("y").forGetter(OffsetType::y),
            RANGE_FLOAT_CODEC.fieldOf("z").forGetter(OffsetType::z)
    ).apply(instance, OffsetType::new));

    public static StreamCodec<RegistryFriendlyByteBuf, OffsetType> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, OffsetType::x,
            ByteBufCodecs.FLOAT, OffsetType::y,
            ByteBufCodecs.FLOAT, OffsetType::z,
            OffsetType::new);

    @Override
    public ModelModifierType<OffsetType> type() {
        return ExtraModelModifierTypes.OFFSET.get();
    }
}
