package com.lovetropics.extras.template.item.modifers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import javax.annotation.Nullable;
import java.util.List;


public final class STTLootWeaponItemTemplateModifier extends LootItemConditionalFunction {

    public static final MapCodec<STTLootWeaponItemTemplateModifier> MAP_CODEC = RecordCodecBuilder.mapCodec(i ->
            commonFields(i).and(
                    i.group(
                            Codec.FLOAT.fieldOf("attack_damage").forGetter(modifier -> modifier.attackDamage),
                            Codec.FLOAT.fieldOf("attack_speed").forGetter(modifier -> modifier.attackSpeed),
                            Codec.BOOL.fieldOf("unbreakable").forGetter(modifier -> modifier.unbreakable)
                    )
            ).apply(i, STTLootWeaponItemTemplateModifier::new)
    );

    private final Float attackDamage;
    private final Float attackSpeed;
    private final boolean unbreakable;

    public STTLootWeaponItemTemplateModifier(List<LootItemCondition> predicates, Float attackDamage, Float attackSpeed, boolean unbreakable) {
        super(predicates);
        this.attackDamage = attackDamage;
        this.attackSpeed = attackSpeed;
        this.unbreakable = unbreakable;
    }

    @Override
    public MapCodec<STTLootWeaponItemTemplateModifier> codec() {
        return MAP_CODEC;
    }

    @Override
    protected ItemStack run(ItemStack itemStack, LootContext context) {
        if (unbreakable) {
            itemStack.set(DataComponents.UNBREAKABLE, Unit.INSTANCE);
        }

        itemStack.set(DataComponents.ATTRIBUTE_MODIFIERS, new ItemAttributeModifiers(List.of(
            new ItemAttributeModifiers.Entry(
                Attributes.ATTACK_DAMAGE,
                new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, (double) attackDamage, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND
            ),
            new ItemAttributeModifiers.Entry(
                Attributes.ATTACK_SPEED,
                new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, (double) attackSpeed, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND
            )
        )));

        TooltipDisplay tooltipDisplay = itemStack.has(DataComponents.TOOLTIP_DISPLAY) ? itemStack.get(DataComponents.TOOLTIP_DISPLAY) : TooltipDisplay.DEFAULT;
        itemStack.set(DataComponents.TOOLTIP_DISPLAY, tooltipDisplay
                .withHidden(DataComponents.ATTRIBUTE_MODIFIERS, true)
                .withHidden(DataComponents.ENCHANTMENTS, true)
        );

        return itemStack;
    }
}
