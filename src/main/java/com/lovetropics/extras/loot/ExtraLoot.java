package com.lovetropics.extras.loot;

import com.lovetropics.extras.LTExtras;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ExtraLoot {

    public static final DeferredRegister<MapCodec<? extends LootPoolEntryContainer>> REGISTER = DeferredRegister.create(Registries.LOOT_POOL_ENTRY_TYPE, LTExtras.MODID);

    public static final DeferredHolder<MapCodec<? extends LootPoolEntryContainer>, MapCodec<LootItemTemplateEntry>> LOOT_TEMPLATE =
            REGISTER.register("loot_template", () -> LootItemTemplateEntry.CODEC);
}
