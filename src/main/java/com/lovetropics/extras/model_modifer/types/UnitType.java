package com.lovetropics.extras.model_modifer.types;

import com.lovetropics.extras.model_modifer.ModelModifier;
import com.lovetropics.extras.model_modifer.ModelModifierType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public final class UnitType implements ModelModifierType<UnitType>, ModelModifier<UnitType> {

    private final MapCodec<UnitType> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, UnitType> streamCodec;

    public UnitType() {
        this.codec = MapCodec.unit(this);
        this.streamCodec = StreamCodec.unit(this);
    }

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
