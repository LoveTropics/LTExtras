package com.lovetropics.extras.item;

import com.lovetropics.extras.ExtraDataComponents;
import com.lovetropics.extras.LTExtras;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemLore;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public class TradingCards {
    public static final List<Preset> PRESETS = IntStream.rangeClosed(2017, 2026).mapToObj(year -> new Preset(
            new ImageData(
                    Optional.empty(),
                    LTExtras.id("textures/images/trading_card/" + year + ".png"),
                    375,
                    525,
                    0.0f,
                    0.0f,
                    List.of()
            ),
            year
    )).toList();

    public record Preset(
            ImageData image,
            int year
    ) {
        public ItemStack create(Item item) {
            ItemStack stack = new ItemStack(item);
            stack.set(ExtraDataComponents.IMAGE.get(), image);
            stack.set(DataComponents.RARITY, Rarity.EPIC);
            stack.set(DataComponents.LORE, new ItemLore(List.of(
                    Component.literal(String.valueOf(year)).withStyle(s -> s.withItalic(false).withColor(ChatFormatting.GOLD))
            )));
            return stack;
        }
    }
}
