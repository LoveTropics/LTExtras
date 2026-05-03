package com.lovetropics.extras.model_modifer;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public interface ModelModifierType<T extends ModelModifier<?>> {

    MapCodec<T> codec();

    StreamCodec<RegistryFriendlyByteBuf, T> streamCodec();

    default boolean canApplyTo(Entity entity) {
        return entity instanceof LivingEntity;
    }

}
