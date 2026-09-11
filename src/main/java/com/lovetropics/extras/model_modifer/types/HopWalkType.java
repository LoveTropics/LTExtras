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
import net.minecraft.util.ExtraCodecs;

import java.util.Optional;

public record HopWalkType(float hopAmount) implements ModelModifier<HopWalkType> {

    private static final float DEFAULT_HOP_AMOUNT = 3.5f;

    public static final MapCodec<HopWalkType> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("amount", HopWalkType.DEFAULT_HOP_AMOUNT).forGetter(HopWalkType::hopAmount)
    ).apply(instance, HopWalkType::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, HopWalkType> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, HopWalkType::hopAmount,
            HopWalkType::new
    );

    public HopWalkType() {
        this(DEFAULT_HOP_AMOUNT);
    }

    @Override
    public ModelModifierType<HopWalkType> type() {
        return ExtraModelModifierTypes.HOP_WALK.get();
    }

}
