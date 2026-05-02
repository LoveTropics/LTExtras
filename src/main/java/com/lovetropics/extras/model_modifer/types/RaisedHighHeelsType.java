package com.lovetropics.extras.model_modifer.types;

import com.lovetropics.extras.model_modifer.ExtraModelModifierTypes;
import com.lovetropics.extras.model_modifer.ModelModifierType;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class RaisedHighHeelsType extends UnitType {

    public static final RaisedHighHeelsType INSTANCE = new RaisedHighHeelsType();
    public static final MapCodec<UnitType> CODEC = MapCodec.unit(INSTANCE);
    public static final StreamCodec<RegistryFriendlyByteBuf, UnitType> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public ModelModifierType<UnitType> type() {
        return ExtraModelModifierTypes.RAISED_HIGH_HEELS.get();
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
