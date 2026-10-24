package com.lovetropics.extras.template.item;

import com.lovetropics.extras.data.Named;
import com.lovetropics.extras.template.TemplateConfigs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;
import java.util.function.Consumer;

public class ItemTemplateLootEntry extends LootPoolSingletonContainer {

    public static final MapCodec<ItemTemplateLootEntry> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Identifier.CODEC.fieldOf("template").forGetter(e -> e.templateName))
            .and(singletonFields(i))
            .apply(i, ItemTemplateLootEntry::new)
    );

    private final Identifier templateName;

    private ItemTemplateLootEntry(Identifier templateName, int weight, int quality, List<LootItemCondition> conditions, List<LootItemFunction> functions) {
        super(weight, quality, conditions, functions);
        this.templateName = templateName;
    }

    @Override
    public MapCodec<ItemTemplateLootEntry> codec() {
        return CODEC;
    }

    @Override
    public void createItemStack(Consumer<ItemStack> output, LootContext context) {
        Named<ItemTemplate> itemTemplate = TemplateConfigs.ITEM_TEMPLATES.get(templateName);
        if (itemTemplate == null) {
            throw new IllegalStateException("Item template not found: " + templateName);
        }
        output.accept(itemTemplate.value().createItemStack(context));
    }
}
