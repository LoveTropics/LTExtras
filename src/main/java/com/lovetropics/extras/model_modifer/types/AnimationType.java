package com.lovetropics.extras.model_modifer.types;

import com.lovetropics.extras.model_modifer.ExtraModelModifierTypes;
import com.lovetropics.extras.model_modifer.ModelModifier;
import com.lovetropics.extras.model_modifer.ModelModifierType;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public record AnimationType(Identifier id) implements ModelModifier<AnimationType> {

    public static final MapCodec<AnimationType> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Identifier.CODEC.fieldOf("animation").forGetter(AnimationType::id)
    ).apply(instance, AnimationType::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AnimationType> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, AnimationType::id,
            AnimationType::new
    );

    @Override
    public ModelModifierType<AnimationType> type() {
        return ExtraModelModifierTypes.ANIMATION.get();
    }
}
