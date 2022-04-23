package com.lovetropics.extras.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.SixWayBlock;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import net.minecraft.block.AbstractBlock.Properties;

public final class ThornStemBlock extends SixWayBlock implements IWaterLoggable {
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
    public void entityInside(BlockState state, World world, BlockPos pos, Entity entity) {
        entity.hurt(DamageSource.SWEET_BERRY_BUSH, 1.0F);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        World world = context.getLevel();
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
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
        if (state.getValue(WATERLOGGED)) {
            world.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }

        BooleanProperty property = PROPERTY_BY_DIRECTION.get(facing);
        boolean connected = this.canConnectTo(world, facingPos, facing.getOpposite());
        return state.setValue(property, connected);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    private boolean canConnectAlong(IBlockReader world, BlockPos pos, Direction direction) {
        BlockPos adjacentPos = pos.relative(direction);
        BlockState adjacentState = world.getBlockState(adjacentPos);
        return this.canConnectTo(adjacentState, world, adjacentPos, direction);
    }

    private boolean canConnectTo(IBlockReader world, BlockPos pos, Direction direction) {
        return this.canConnectTo(world.getBlockState(pos), world, pos, direction);
    }

    private boolean canConnectTo(BlockState state, IBlockReader world, BlockPos pos, Direction direction) {
        return state.is(this)
                || state.isFaceSturdy(world, pos, direction);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN, WATERLOGGED);
    }

    @Override
    public boolean isPathfindable(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
        return false;
    }
}
