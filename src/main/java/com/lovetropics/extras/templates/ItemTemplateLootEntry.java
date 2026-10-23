package com.lovetropics.extras.templates;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.data.Named;
import com.lovetropics.extras.registry.ExtraRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class ItemTemplateLootEntry extends LootPoolSingletonContainer {
    public static final MapCodec<ItemTemplateLootEntry> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.STRING.fieldOf("template").forGetter(e -> e.itemTemplateName))
            .and(singletonFields(i))
            .apply(i, ItemTemplateLootEntry::new)
    );

    private final String itemTemplateName;

    private ItemTemplateLootEntry(String itemTemplateName, int weight, int quality, List<LootItemCondition> conditions, List<LootItemFunction> functions) {
        super(weight, quality, conditions, functions);
        this.itemTemplateName = itemTemplateName;
    }

    public MapCodec<ItemTemplateLootEntry> codec() {
        return CODEC;
    }

    public void createItemStack(Consumer<ItemStack> output, LootContext context) {
        RegistryAccess.Frozen registryAccess = context.getLevel().getServer().registryAccess();
        Registry<ItemTemplate> registry = registryAccess.lookupOrThrow(ExtraRegistries.ITEM_TEMPLATE);

        Optional<Holder.Reference<ItemTemplate>> itemTemplate = registry.get(Identifier.parse(itemTemplateName));
        output.accept(itemTemplate.orElseThrow().value().createItemStack());
    }

    public static LootPoolSingletonContainer.Builder<?> lootTableLootItem(String templateName) {
        return simpleBuilder((weight, quality, conditions, functions) -> new ItemTemplateLootEntry(templateName, weight, quality, conditions, functions));
    }
}
