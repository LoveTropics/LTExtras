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

public record OffsetType() implements ModelModifierType<OffsetType.Modifier> {

    public record Modifier(float x, float y, float z) implements ModelModifier<Modifier> {

        private static final Codec<Float> RANGE_FLOAT_CODEC = Codec.floatRange(1, 15);

        public static final MapCodec<Modifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                RANGE_FLOAT_CODEC.fieldOf("x").forGetter(Modifier::x),
                RANGE_FLOAT_CODEC.fieldOf("y").forGetter(Modifier::y),
                RANGE_FLOAT_CODEC.fieldOf("z").forGetter(Modifier::z)
        ).apply(instance, Modifier::new));

        public static StreamCodec<RegistryFriendlyByteBuf, Modifier> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.FLOAT, Modifier::x,
                ByteBufCodecs.FLOAT, Modifier::y,
                ByteBufCodecs.FLOAT, Modifier::z,
                Modifier::new);

        @Override
        public ModelModifierType<Modifier> type() {
            return ExtraModelModifierTypes.OFFSET.get();
        }
    }

    @Override
    public MapCodec<Modifier> codec() {
       return Modifier.CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, Modifier> streamCodec() {
        return Modifier.STREAM_CODEC;
    }
}
