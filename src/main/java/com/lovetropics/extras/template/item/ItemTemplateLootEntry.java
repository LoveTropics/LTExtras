package com.lovetropics.extras.template.item;

import com.lovetropics.extras.data.Named;
import com.lovetropics.extras.template.TemplateConfigs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
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

    public ItemTemplateLootEntry(Identifier templateName, int weight, int quality, List<LootItemCondition> conditions, List<LootItemFunction> functions) {
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

    public static Builder builder(Identifier templateName) {
        return new Builder(templateName);
    }

    public static class Builder extends LootPoolSingletonContainer.Builder<Builder> {
        private final Identifier templateName;

        public Builder(Identifier templateName) {
            this.templateName = templateName;
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public LootPoolEntryContainer build() {
            return new ItemTemplateLootEntry(templateName, weight, quality, getConditions(), getFunctions());
        }
    }
}
