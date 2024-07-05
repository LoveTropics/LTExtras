package com.lovetropics.extras.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;

public class PanelBlock extends DirectionalBlock {
	public static final MapCodec<PanelBlock> CODEC = simpleCodec(PanelBlock::new);

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
	protected MapCodec<? extends DirectionalBlock> codec() {
		return CODEC;
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return super.getStateForPlacement(context).setValue(FACING, context.getClickedFace().getOpposite());
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return SHAPES.get(state.getValue(FACING));
	}
}
