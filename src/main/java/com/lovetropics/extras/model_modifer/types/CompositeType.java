package com.lovetropics.extras.model_modifer.types;

import com.lovetropics.extras.model_modifer.ExtraModelModifierTypes;
import com.lovetropics.extras.model_modifer.ModelModifier;
import com.lovetropics.extras.model_modifer.ModelModifierType;
import com.lovetropics.extras.model_modifer.ExtraModelModifiers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.List;

public record CompositeType(List<ModelModifier<?>> modifiers) implements ModelModifier<CompositeType> {

    public static final MapCodec<CompositeType> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ExtraModelModifiers.CODEC.listOf().fieldOf("modifiers").forGetter(CompositeType::modifiers)
    ).apply(instance, CompositeType::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CompositeType> STREAM_CODEC = StreamCodec.composite(
            ExtraModelModifiers.DIRECT_STREAM_CODEC.apply(ByteBufCodecs.list()), CompositeType::modifiers,
            CompositeType::new
    );

    @Override
    public ModelModifierType<CompositeType> type() {
        return ExtraModelModifierTypes.COMPOSITE.get();
    }

    public static class Builder {
        private final List<ModelModifier<?>> modifiers = new ArrayList<>();

        public static Builder builder() {
            return new Builder();
        }

        public Builder add(ModelModifier<?> modifier) {
            this.modifiers.add(modifier);
            return this;
        }

        public CompositeType build() {
            return new CompositeType(modifiers);
        }
    }

}
