package com.lovetropics.extras.model_modifer.types;

import com.lovetropics.extras.model_modifer.ExtraModelModifierTypes;
import com.lovetropics.extras.model_modifer.ModelModifierType;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class HopWalkType extends UnitType {

    public static final HopWalkType INSTANCE = new HopWalkType();
    public static final MapCodec<UnitType> CODEC = MapCodec.unit(INSTANCE);
    public static final StreamCodec<RegistryFriendlyByteBuf, UnitType> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public ModelModifierType<UnitType> type() {
        return ExtraModelModifierTypes.HOP_WALK.get();
    }

    @Override
    public MapCodec<UnitType> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, UnitType> streamCodec() {
        return STREAM_CODEC;
    }
}
