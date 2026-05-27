package com.lovetropics.extras.model_modifer.types;

import com.lovetropics.extras.model_modifer.ModelModifier;
import com.lovetropics.extras.model_modifer.ModelModifierType;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class UnitType implements ModelModifier<UnitType>, ModelModifierType<UnitType> {

    private final MapCodec<UnitType> codec = MapCodec.unit(this);
    private final StreamCodec<RegistryFriendlyByteBuf, UnitType> streamCodec = StreamCodec.unit(this);

    @Override
    public ModelModifierType<UnitType> type() {
        return this;
    }

    @Override
    public MapCodec<UnitType> codec() {
        return codec;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, UnitType> streamCodec() {
        return streamCodec;
    }
}
