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

public record OffsetType() implements ModelModifierType<OffsetType.Data> {

    public record Data(float x, float y, float z) implements ModelModifier<Data> {
        public static final MapCodec<Data> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.FLOAT.fieldOf("x").forGetter(Data::x),
                Codec.FLOAT.fieldOf("y").forGetter(Data::y),
                Codec.FLOAT.fieldOf("z").forGetter(Data::z)
        ).apply(instance, Data::new));

        public static StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.FLOAT, Data::x,
                ByteBufCodecs.FLOAT, Data::y,
                ByteBufCodecs.FLOAT, Data::z,
                Data::new);

        @Override
        public Data data() {
            return this;
        }

        @Override
        public ModelModifierType<Data> type() {
            return ExtraModelModifierTypes.OFFSET.get();
        }
    }

    @Override
    public MapCodec<OffsetType.Data> codec() {
       return Data.CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, Data> streamCodec() {
        return Data.STREAM_CODEC;
    }
}
