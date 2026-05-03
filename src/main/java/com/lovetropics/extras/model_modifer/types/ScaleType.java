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

import java.util.Optional;

public record ScaleType() implements ModelModifierType<ScaleType.Modifier> {

    public record Modifier(Optional<Float> scaleX, Optional<Float>  scaleY, Optional<Float> scaleZ) implements ModelModifier<Modifier> {
        public static final MapCodec<Modifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.FLOAT.optionalFieldOf("scaleX").forGetter(Modifier::scaleX),
                Codec.FLOAT.optionalFieldOf("scaleY").forGetter(Modifier::scaleY),
                Codec.FLOAT.optionalFieldOf("scaleZ").forGetter(Modifier::scaleZ)
        ).apply(instance, Modifier::new));

        public static StreamCodec<RegistryFriendlyByteBuf, Modifier> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.FLOAT.apply(ByteBufCodecs::optional), Modifier::scaleX,
                ByteBufCodecs.FLOAT.apply(ByteBufCodecs::optional), Modifier::scaleY,
                ByteBufCodecs.FLOAT.apply(ByteBufCodecs::optional), Modifier::scaleZ,
                Modifier::new);

        @Override
        public ModelModifierType<Modifier> type() {
            return ExtraModelModifierTypes.SCALE.get();
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
