package com.lovetropics.extras.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public record PaintingOverlay(
    Optional<ItemStack> itemStack,
    Optional<Component> text,
    Holder<PaintingVariant> painting
) {
    public static final Codec<PaintingOverlay> CODEC = RecordCodecBuilder.create(i -> i.group(
            ItemStack.CODEC.optionalFieldOf("display_itemstack").forGetter(PaintingOverlay::itemStack),
            ComponentSerialization.CODEC.optionalFieldOf("display_text").forGetter(PaintingOverlay::text),
            PaintingVariant.CODEC.fieldOf("painting").forGetter(PaintingOverlay::painting)
    ).apply(i, PaintingOverlay::new));
}
