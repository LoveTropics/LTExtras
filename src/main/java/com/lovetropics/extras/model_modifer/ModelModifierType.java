package com.lovetropics.extras.model_modifer;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface ModelModifierType<T extends ModelModifier<?>> {
    MapCodec<T> codec();

    StreamCodec<RegistryFriendlyByteBuf, T> streamCodec();

    static <T extends ModelModifier<?>> Simple<T> simple(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
        return new Simple<>(codec, streamCodec);
    }

    record Simple<T extends ModelModifier<?>> (MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) implements ModelModifierType<T> {}
}
