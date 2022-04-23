package com.lovetropics.extras.block;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

import net.minecraft.block.AbstractBlock.Properties;

public class PanelBlock extends DirectionalBlock {

	public static final Map<Direction, VoxelShape> SHAPES = Maps.immutableEnumMap(
			ImmutableMap.<Direction, VoxelShape>builder()
				.put(Direction.EAST, Block.box(13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D))
				.put(Direction.WEST, Block.box(0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D))
				.put(Direction.SOUTH, Block.box(0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D))
				.put(Direction.NORTH, Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D))
				.put(Direction.DOWN, Block.box(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D))
				.put(Direction.UP, Block.box(0.0D, 13.0D, 0.0D, 16.0D, 16.0D, 16.0D))
				.build());

	public PanelBlock(Properties builder) {
		super(builder);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return super.getStateForPlacement(context).setValue(FACING, context.getClickedFace().getOpposite());
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return SHAPES.get(state.getValue(FACING));
	}
}
