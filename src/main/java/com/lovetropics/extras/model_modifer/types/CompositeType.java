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

public class CompositeType implements ModelModifierType<CompositeType.Data> {

    public record Data(List<ModelModifier<?>> modifiers) implements ModelModifier<CompositeType.Data> {

        public static final MapCodec<Data> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ExtraModelModifierTypes.CODEC.listOf().fieldOf("modifiers").forGetter(Data::modifiers)
        ).apply(instance, Data::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
                ExtraModelModifierTypes.DIRECT_STREAM_CODEC.apply(ByteBufCodecs.list()), Data::modifiers,
                Data::new
        );

        @Override
        public CompositeType.Data data() {
            return this;
        }

        @Override
        public ModelModifierType<CompositeType.Data> type() {
            return ExtraModelModifierTypes.COMPOSITE.get();
        }
    }

    @Override
    public MapCodec<CompositeType.Data> codec() {
        return CompositeType.Data.CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, Data> streamCodec() {
        return CompositeType.Data.STREAM_CODEC;
    }
}
