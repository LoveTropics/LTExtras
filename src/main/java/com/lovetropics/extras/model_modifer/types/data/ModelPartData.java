package com.lovetropics.extras.model_modifer.types.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record ModelPartData(
        Optional<Float> x,
        Optional<Float> y,
        Optional<Float> z,
        Optional<Float> xRot,
        Optional<Float> yRot,
        Optional<Float> zRot,
        Optional<Float> xScale,
        Optional<Float> yScale,
        Optional<Float> zScale
) {

    public static final Codec<ModelPartData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.optionalFieldOf("x").forGetter(ModelPartData::x),
            Codec.FLOAT.optionalFieldOf("y").forGetter(ModelPartData::y),
            Codec.FLOAT.optionalFieldOf("z").forGetter(ModelPartData::z),
            Codec.FLOAT.optionalFieldOf("xRot").forGetter(ModelPartData::xRot),
            Codec.FLOAT.optionalFieldOf("yRot").forGetter(ModelPartData::yRot),
            Codec.FLOAT.optionalFieldOf("zRot").forGetter(ModelPartData::zRot),
            Codec.FLOAT.optionalFieldOf("xScale").forGetter(ModelPartData::xScale),
            Codec.FLOAT.optionalFieldOf("yScale").forGetter(ModelPartData::yScale),
            Codec.FLOAT.optionalFieldOf("zScale").forGetter(ModelPartData::zScale)
    ).apply(instance, ModelPartData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ModelPartData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(ByteBufCodecs.FLOAT), ModelPartData::x,
            ByteBufCodecs.optional(ByteBufCodecs.FLOAT), ModelPartData::y,
            ByteBufCodecs.optional(ByteBufCodecs.FLOAT), ModelPartData::z,
            ByteBufCodecs.optional(ByteBufCodecs.FLOAT), ModelPartData::xRot,
            ByteBufCodecs.optional(ByteBufCodecs.FLOAT), ModelPartData::yRot,
            ByteBufCodecs.optional(ByteBufCodecs.FLOAT), ModelPartData::zRot,
            ByteBufCodecs.optional(ByteBufCodecs.FLOAT), ModelPartData::xScale,
            ByteBufCodecs.optional(ByteBufCodecs.FLOAT), ModelPartData::yScale,
            ByteBufCodecs.optional(ByteBufCodecs.FLOAT), ModelPartData::zScale,
            ModelPartData::new);
}
