package com.lovetropics.extras.model_modifer.types;

import com.lovetropics.extras.model_modifer.ExtraModelModifierTypes;
import com.lovetropics.extras.model_modifer.ModelModifier;
import com.lovetropics.extras.model_modifer.ModelModifierType;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public class CompositeType implements ModelModifierType<CompositeType.Modifier> {

    public record Modifier(List<ModelModifier<?>> modifiers) implements ModelModifier<Modifier> {

        public static final MapCodec<Modifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ExtraModelModifierTypes.CODEC.listOf().fieldOf("modifiers").forGetter(Modifier::modifiers)
        ).apply(instance, Modifier::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, Modifier> STREAM_CODEC = StreamCodec.composite(
                ExtraModelModifierTypes.DIRECT_STREAM_CODEC.apply(ByteBufCodecs.list()), Modifier::modifiers,
                Modifier::new
        );

        @Override
        public ModelModifierType<Modifier> type() {
            return ExtraModelModifierTypes.COMPOSITE.get();
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
