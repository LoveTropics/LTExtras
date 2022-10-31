package com.lovetropics.extras.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;

public final class ThornStemBlock extends PipeBlock implements SimpleWaterloggedBlock {
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public ThornStemBlock(Properties properties) {
		super(3.0F / 16.0F, properties);
		this.registerDefaultState(this.getStateDefinition().any()
				.setValue(NORTH, false)
				.setValue(EAST, false)
				.setValue(SOUTH, false)
				.setValue(WEST, false)
				.setValue(UP, false)
				.setValue(DOWN, false)
				.setValue(WATERLOGGED, false)
		);
	}

	@Override
	public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
		entity.hurt(DamageSource.SWEET_BERRY_BUSH, 1.0F);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Level world = context.getLevel();
		BlockPos pos = context.getClickedPos();
		return this.defaultBlockState()
				.setValue(NORTH, this.canConnectAlong(world, pos, Direction.NORTH))
				.setValue(EAST, this.canConnectAlong(world, pos, Direction.EAST))
				.setValue(SOUTH, this.canConnectAlong(world, pos, Direction.SOUTH))
				.setValue(WEST, this.canConnectAlong(world, pos, Direction.WEST))
				.setValue(UP, this.canConnectAlong(world, pos, Direction.UP))
				.setValue(DOWN, this.canConnectAlong(world, pos, Direction.DOWN))
				.setValue(WATERLOGGED, world.getFluidState(pos).getType() == Fluids.WATER);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
		if (state.getValue(WATERLOGGED)) {
			world.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
		}

		BooleanProperty property = PROPERTY_BY_DIRECTION.get(facing);
		boolean connected = this.canConnectTo(world, facingPos, facing.getOpposite());
		return state.setValue(property, connected);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	private boolean canConnectAlong(BlockGetter world, BlockPos pos, Direction direction) {
		BlockPos adjacentPos = pos.relative(direction);
		BlockState adjacentState = world.getBlockState(adjacentPos);
		return this.canConnectTo(adjacentState, world, adjacentPos, direction);
	}

	private boolean canConnectTo(BlockGetter world, BlockPos pos, Direction direction) {
		return this.canConnectTo(world.getBlockState(pos), world, pos, direction);
	}

	private boolean canConnectTo(BlockState state, BlockGetter world, BlockPos pos, Direction direction) {
		return state.is(this)
				|| state.isFaceSturdy(world, pos, direction);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN, WATERLOGGED);
	}

	@Override
	public boolean isPathfindable(BlockState state, BlockGetter worldIn, BlockPos pos, PathComputationType type) {
		return false;
	}
}
