package com.lovetropics.extras.loot;

import com.lovetropics.extras.LTExtras;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * LTExtras
 * FIRE EMOJI, FIRE EMOJI, FIRE EMOJI
 */
public class ExtraLoot {

    public static final DeferredRegister<MapCodec<? extends LootPoolEntryContainer>> REGISTER =
            DeferredRegister.create(Registries.LOOT_POOL_ENTRY_TYPE, LTExtras.MODID);
    public static final Supplier<MapCodec<LootLootEntry>> LOOT_LOOT =
            REGISTER.register("loot_item", () -> LootLootEntry.CODEC);
}
