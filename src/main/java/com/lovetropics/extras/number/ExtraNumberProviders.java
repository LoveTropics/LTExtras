package com.lovetropics.extras.number;

import com.lovetropics.extras.LTExtras;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ExtraNumberProviders {
    public static final DeferredRegister<MapCodec<? extends NumberProvider>> REGISTER = DeferredRegister.create(Registries.LOOT_NUMBER_PROVIDER_TYPE, LTExtras.MODID);

    public static final Holder<MapCodec<? extends NumberProvider>> WEIGHTED_LIST = REGISTER.register("weighted_list", () -> WeightedListNumberProvider.CODEC);
}
