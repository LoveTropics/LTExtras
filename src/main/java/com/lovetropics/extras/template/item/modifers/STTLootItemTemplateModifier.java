package com.lovetropics.extras.template.item.modifers;

import com.lovetropics.extras.translation.TranslationPromptScreen;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.network.chat.Component.translatable;

public final class STTLootItemTemplateModifier extends LootItemConditionalFunction {

    private static final Identifier TOOLTIP_STYLE = Identifier.fromNamespaceAndPath("lt", "loot");

    public static final MapCodec<STTLootItemTemplateModifier> MAP_CODEC = RecordCodecBuilder.mapCodec(i ->
            commonFields(i).and(
                    i.group(
                            Codec.STRING.fieldOf("name").forGetter(modifier -> modifier.name),
                            LootCategory.CODEC.fieldOf("category").forGetter(modifier -> modifier.lootCategory),
                            Codec.INT.optionalFieldOf("tier", 0).forGetter(modifier -> modifier.tier),
                            Codec.INT.optionalFieldOf("description_lines", 0).forGetter(modifier -> modifier.descriptionLines),
                            Codec.STRING.listOf().optionalFieldOf("modifier_tooltips", List.of()).forGetter(modifier -> modifier.modifierTooltips),
                            Codec.BOOL.optionalFieldOf("show_divider", true).forGetter(modifier -> modifier.showDivider)
                    )
            ).apply(i, STTLootItemTemplateModifier::new)
    );

    private final String name;
    private final LootCategory lootCategory;
    private final int tier;
    private final int descriptionLines;
    private final List<String> modifierTooltips;
    private final boolean showDivider;

    public STTLootItemTemplateModifier(List<LootItemCondition> predicates, String name, LootCategory lootCategory, int tier, int descriptionLines, List<String> modifierTooltips, boolean showDivider) {
        super(predicates);
        this.name = name;
        this.lootCategory = lootCategory;
        this.tier = tier;
        this.descriptionLines = descriptionLines;
        this.modifierTooltips = modifierTooltips;
        this.showDivider = showDivider;
    }

    @Override
    public MapCodec<STTLootItemTemplateModifier> codec() {
        return MAP_CODEC;
    }

    @Override
    protected ItemStack run(ItemStack itemStack, LootContext context) {
        itemStack.set(DataComponents.CUSTOM_NAME, getFormattedName());

        List<Component> loreText = getDescriptionLines();
        loreText.add(Component.literal(""));
        loreText.add(lootCategory.getTranslatedLozenge().withStyle(Style.EMPTY.withItalic(false)));
        if (showDivider) {
            loreText.add(Component.literal("\uE060").withColor(TextColor.WHITE).withStyle(Style.EMPTY.withItalic(false)));
        }
        modifierTooltips.forEach(tooltip -> loreText.add(getModifierTooltip(tooltip)));
        itemStack.set(DataComponents.LORE, new ItemLore(loreText));
        itemStack.set(DataComponents.TOOLTIP_STYLE, TOOLTIP_STYLE);

        TooltipDisplay tooltipDisplay = itemStack.has(DataComponents.TOOLTIP_DISPLAY) ? itemStack.get(DataComponents.TOOLTIP_DISPLAY) : TooltipDisplay.DEFAULT;
        itemStack.set(DataComponents.TOOLTIP_DISPLAY, tooltipDisplay
                .withHidden(DataComponents.POTION_CONTENTS, true)
                .withHidden(DataComponents.UNBREAKABLE, true)
        );

        // Specific override for potions so they don't give a bottle when consumed.
        // Truly there must be a better way to check if item of this stack is some other item, like am I crazy for that??
        if (itemStack.getItem().equals(Items.POTION)) {
            itemStack.remove(DataComponents.USE_REMAINDER);
        }

        return itemStack;
    }

    private Component getFormattedName() {
        // Very simple check if provided name value is that of an existing minecraft lang key.
        if (name.contains(".minecraft.")) {
            return lootCategory.getTranslatedIcon(tier).append(" | ").append(translatable(name).withColor(lootCategory.getColor())).withStyle(Style.EMPTY.withItalic(false));
        }

        return lootCategory.getTranslatedIcon(tier).append(" | ").append(translatable("lt.loot." + name).withColor(lootCategory.getColor())).withStyle(Style.EMPTY.withItalic(false));
    }

    private List<Component> getDescriptionLines() {
        List<Component> lines = new ArrayList<>();
        if (descriptionLines <= 0) {
            return lines;
        }
        for (int i = 0; i < descriptionLines; i++) {
            lines.add(translatable("lt.loot." + name + ".description." + (i + 1)).withColor(TextColor.GRAY).withStyle(Style.EMPTY.withItalic(false)));
        }
        return lines;
    }

    private Component getModifierTooltip(String name) {
        return translatable("lt.loot_modifier." + name).withColor(TextColor.BLUE).withStyle(Style.EMPTY.withItalic(false));
    }
}
