package com.lovetropics.extras.block;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

// sorry
public class ScientificNameBlock extends Block {
	private final String scientificName;

	public ScientificNameBlock(Properties properties, String scientificName) {
		super(properties);
		this.scientificName = scientificName;
	}

	@Override
	public void appendHoverText(ItemStack itemStack, @Nullable Item.TooltipContext ctx, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(Component.literal(scientificName).withStyle(ChatFormatting.AQUA, ChatFormatting.ITALIC));
	}
}
