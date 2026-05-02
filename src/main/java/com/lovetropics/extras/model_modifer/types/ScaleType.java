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

public record ScaleType() implements ModelModifierType<ScaleType.Data> {

    public record Data(float scaleX, float scaleY, float scaleZ) implements ModelModifier<Data> {
        public static final MapCodec<Data> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.FLOAT.fieldOf("scaleX").forGetter(Data::scaleX),
                Codec.FLOAT.fieldOf("scaleY").forGetter(Data::scaleY),
                Codec.FLOAT.fieldOf("scaleZ").forGetter(Data::scaleZ)
        ).apply(instance, Data::new));

        public static StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.FLOAT, Data::scaleX,
                ByteBufCodecs.FLOAT, Data::scaleY,
                ByteBufCodecs.FLOAT, Data::scaleZ,
                Data::new);

        @Override
        public Data data() {
            return this;
        }

        @Override
        public ModelModifierType<Data> type() {
            return ExtraModelModifierTypes.SCALE.get();
        }
    }

    @Override
    public MapCodec<ScaleType.Data> codec() {
       return Data.CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, Data> streamCodec() {
        return Data.STREAM_CODEC;
    }
}
