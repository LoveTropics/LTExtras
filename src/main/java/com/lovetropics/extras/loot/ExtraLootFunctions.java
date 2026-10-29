package com.lovetropics.extras.loot;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.loot.functions.SelectRandomFunction;
import com.lovetropics.extras.template.item.modifers.STTLootItemTemplateModifier;
import com.lovetropics.extras.template.item.modifers.STTLootWeaponItemTemplateModifier;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ExtraLootFunctions {
    public static final DeferredRegister<MapCodec<? extends LootItemFunction>> REGISTRY = DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, LTExtras.MODID);

    public static final DeferredHolder<MapCodec<? extends LootItemFunction>, MapCodec<SelectRandomFunction>> SELECT_RANDOM = REGISTRY.register("select_random", () -> SelectRandomFunction.MAP_CODEC);
    public static final DeferredHolder<MapCodec<? extends LootItemFunction>, MapCodec<STTLootItemTemplateModifier>> STT_LOOT_ITEM_TEMPLATE_MODIFIER = REGISTRY.register("stt_loot", () -> STTLootItemTemplateModifier.MAP_CODEC);
    public static final DeferredHolder<MapCodec<? extends LootItemFunction>, MapCodec<STTLootWeaponItemTemplateModifier>> STT_LOOT_WEAPON_ITEM_TEMPLATE_MODIFIER = REGISTRY.register("stt_loot_weapon", () -> STTLootWeaponItemTemplateModifier.MAP_CODEC);
}
