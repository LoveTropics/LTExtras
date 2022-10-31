package com.lovetropics.extras.block;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

// sorry
public class ScientificNameBlock extends Block {
	private final String scientificName;

	public ScientificNameBlock(final Properties properties, final String scientificName) {
		super(properties);
		this.scientificName = scientificName;
	}

	@Override
	public void appendHoverText(final ItemStack itemStack, final @Nullable BlockGetter level, final List<Component> tooltip, final TooltipFlag flag) {
		tooltip.add(new TextComponent(scientificName).withStyle(ChatFormatting.AQUA, ChatFormatting.ITALIC));
	}
}
