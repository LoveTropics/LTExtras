package com.lovetropics.extras.block;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SeagrassBlock;
import net.minecraft.world.level.block.TallSeagrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class CustomSeagrassBlock extends SeagrassBlock {

	private final String scientificName;
	@Nullable
	private final Supplier<Supplier<? extends TallSeagrassBlock>> tall;

	public CustomSeagrassBlock(final Properties properties, final String scientificName, @Nullable final Supplier<Supplier<? extends TallSeagrassBlock>> tall) {
		super(properties);
		this.scientificName = scientificName;
		this.tall = tall;
	}

	@Override
	public void appendHoverText(final ItemStack itemStack, @Nullable final Item.TooltipContext ctx, final List<Component> tooltip, final TooltipFlag flag) {
		tooltip.add(Component.literal(scientificName).withStyle(ChatFormatting.AQUA, ChatFormatting.ITALIC));
	}

	@Override
	public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
		if (tall == null) return;

		BlockState bottomState = tall.get().get().defaultBlockState();
		BlockState topState = bottomState.setValue(TallSeagrassBlock.HALF, DoubleBlockHalf.UPPER);

		BlockPos topPos = pos.above();
		if (level.getBlockState(topPos).is(Blocks.WATER)) {
			level.setBlock(pos, bottomState, Block.UPDATE_CLIENTS);
			level.setBlock(topPos, topState, Block.UPDATE_CLIENTS);
		}
	}
}
