package com.lovetropics.extras.templates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

import java.util.List;

/**
 * LTExtras
 * FIRE EMOJI, FIRE EMOJI, FIRE EMOJI
 */
public record ItemTemplate(
        ItemStackTemplate item,
        List<ItemTemplateModifier> modifiers
) {
    public static final Codec<ItemTemplate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStackTemplate.CODEC.fieldOf("item").forGetter(ItemTemplate::item),
            ItemTemplateModifier.CODEC.listOf().optionalFieldOf("modifiers", List.of()).forGetter(ItemTemplate::modifiers)
    ).apply(instance, ItemTemplate::new));

    public ItemStack createItemStack() {
        ItemStack itemStack = item.create();
        for (ItemTemplateModifier modifier : modifiers) {
            modifier.apply(itemStack);
        }

        return itemStack;
    }
}
