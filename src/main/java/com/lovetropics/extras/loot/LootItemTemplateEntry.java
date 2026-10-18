package com.lovetropics.extras.loot;

import com.lovetropics.extras.data.Named;
import com.lovetropics.extras.templates.TemplateConfigs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;
import java.util.function.Consumer;

public class LootItemTemplateEntry extends LootPoolSingletonContainer {
    public static final MapCodec<LootItemTemplateEntry> CODEC = RecordCodecBuilder.mapCodec(i ->
            i.group(TemplateConfigs.ITEM_TEMPLATE.fieldOf("name").forGetter(e -> e.lootItem))
            // Add common fields: weight, display, conditions, and functions.
            .and(singletonFields(i))
            .apply(i, LootItemTemplateEntry::new)
    );

    private final Named<ItemStackTemplate> lootItem;

    private LootItemTemplateEntry(Named<ItemStackTemplate> lootItem, int weight, int quality, List<LootItemCondition> conditions, List<LootItemFunction> functions) {
        super(weight, quality, conditions, functions);
        this.lootItem = lootItem;
    }

    public MapCodec<LootItemTemplateEntry> codec() {
        return CODEC;
    }

    public void createItemStack(Consumer<ItemStack> output, LootContext context) {
        output.accept(this.lootItem.value().create());
    }

    public static LootPoolSingletonContainer.Builder<?> lootTableLootItem(Named<ItemStackTemplate> lootItem) {
        return simpleBuilder((weight, quality, conditions, functions) -> new LootItemTemplateEntry(lootItem, weight, quality, conditions, functions));
    }
}
