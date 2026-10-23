package com.lovetropics.extras.templates.modifiers.loot;

import com.lovetropics.extras.templates.ItemTemplateModifier;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.network.chat.Component.translatable;

public record LootItemTemplateModifier(
        String name,
        LootCategory lootCategory,
        int tier,
        int descriptionLines,
        List<String> modifierTooltips
) implements ItemTemplateModifier {
    public static final MapCodec<LootItemTemplateModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(LootItemTemplateModifier::name),
            LootCategory.CODEC.fieldOf("category").forGetter(LootItemTemplateModifier::lootCategory),
            Codec.INT.optionalFieldOf("tier", 0).forGetter(LootItemTemplateModifier::tier),
            Codec.INT.optionalFieldOf("description_lines", 0).forGetter(LootItemTemplateModifier::descriptionLines),
            Codec.STRING.listOf().optionalFieldOf("modifier_tooltips", List.of()).forGetter(LootItemTemplateModifier::modifierTooltips)
    ).apply(instance, LootItemTemplateModifier::new));

    @Override
    public MapCodec<LootItemTemplateModifier> codec() {
        return CODEC;
    }

    @Override
    public void apply(ItemStack itemStack) {
        itemStack.set(DataComponents.CUSTOM_NAME, getFormattedName());

        List<Component> loreText = getDescriptionLines();
        loreText.add(Component.literal(""));
        loreText.add(lootCategory.getTranslatedLozenge().withStyle(Style.EMPTY.withItalic(false)));
        loreText.add(Component.literal("\uE060").withColor(TextColor.WHITE).withStyle(Style.EMPTY.withItalic(false)));
        modifierTooltips.forEach(tooltip -> loreText.add(getModifierTooltip(tooltip)));
        itemStack.set(DataComponents.LORE, new ItemLore(loreText));
        itemStack.set(DataComponents.TOOLTIP_STYLE, Identifier.parse("lt:loot"));
        itemStack.set(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT
                .withHidden(DataComponents.POTION_CONTENTS, true)
                .withHidden(DataComponents.UNBREAKABLE, true)
        );

    }

    private Component getFormattedName() {
        return lootCategory.getTranslatedIcon(tier).append(" | ").append(translatable("lt.loot." + name).withColor(lootCategory.getColor())).withStyle(Style.EMPTY.withItalic(false));
    }

    private List<Component> getDescriptionLines() {
        List<Component> lines = new ArrayList<>();
        if (descriptionLines <= 0) return lines;
        for (int i = 0; i < descriptionLines; i++) {
            lines.add(translatable("lt.loot." + name + ".description." + (i+1)).withColor(TextColor.GRAY).withStyle(Style.EMPTY.withItalic(false)));
        }
        return lines;
    }

    private Component getModifierTooltip(String name) {
        return translatable("lt.loot_modifier." + name).withColor(TextColor.BLUE).withStyle(Style.EMPTY.withItalic(false));
    }

}
