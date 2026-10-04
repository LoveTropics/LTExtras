package com.lovetropics.extras.functions;

import com.lovetropics.extras.LTExtras;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ExtraFunctions {
    public static final DeferredRegister<MapCodec<? extends LootItemFunction>> REGISTRY = DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, LTExtras.MODID);

    public static final DeferredHolder<MapCodec<? extends LootItemFunction>, MapCodec<SelectRandomFunction>> SELECT_RANDOM = REGISTRY.register("select_random", () -> SelectRandomFunction.MAP_CODEC);
}
