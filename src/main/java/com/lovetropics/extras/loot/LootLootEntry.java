package com.lovetropics.extras.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.List;
import java.util.function.Consumer;

/**
 * LTExtras
 * FIRE EMOJI, FIRE EMOJI, FIRE EMOJI
 */
public class LootLootEntry extends LootPoolSingletonContainer {
    public static final MapCodec<LootLootEntry> CODEC = RecordCodecBuilder.mapCodec(i ->
            i.group(LootItem.CODEC.fieldOf("name").forGetter(e -> e.lootItem))
            // Add common fields: weight, display, conditions, and functions.
            .and(singletonFields(i))
            .apply(i, LootLootEntry::new)
    );

    private final Holder<LootItem> lootItem;

    private LootLootEntry(Holder<LootItem> lootItem, int weight, int quality, List<LootItemCondition> conditions, List<LootItemFunction> functions) {
        super(weight, quality, conditions, functions);
        this.lootItem = lootItem;
    }

    public MapCodec<LootLootEntry> codec() {
        return CODEC;
    }

    public void createItemStack(Consumer<ItemStack> output, LootContext context) {
        output.accept(LootItem.createItemStack(this.lootItem));
    }

    public static LootPoolSingletonContainer.Builder<?> lootTableLootItem(Holder<LootItem> lootItem) {
        return simpleBuilder((weight, quality, conditions, functions) -> new LootLootEntry(lootItem, weight, quality, conditions, functions));
    }
}
