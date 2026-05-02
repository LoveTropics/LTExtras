package com.lovetropics.extras.model_modifer;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface ModelModifierType<T extends ModelModifier<?>> {

//    T create();

    MapCodec<T> codec();

    StreamCodec<RegistryFriendlyByteBuf, T> streamCodec();

}
