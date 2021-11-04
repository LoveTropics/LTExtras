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

public final class ThornStemBlock extends SixWayBlock implements IWaterLoggable {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public ThornStemBlock(Properties properties) {
        super(3.0F / 16.0F, properties);
        this.setDefaultState(this.getStateContainer().getBaseState()
                .with(NORTH, false)
                .with(EAST, false)
                .with(SOUTH, false)
                .with(WEST, false)
                .with(UP, false)
                .with(DOWN, false)
                .with(WATERLOGGED, false)
        );
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        entity.attackEntityFrom(DamageSource.SWEET_BERRY_BUSH, 1.0F);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        return this.getDefaultState()
                .with(NORTH, this.canConnectAlong(world, pos, Direction.NORTH))
                .with(EAST, this.canConnectAlong(world, pos, Direction.EAST))
                .with(SOUTH, this.canConnectAlong(world, pos, Direction.SOUTH))
                .with(WEST, this.canConnectAlong(world, pos, Direction.WEST))
                .with(UP, this.canConnectAlong(world, pos, Direction.UP))
                .with(DOWN, this.canConnectAlong(world, pos, Direction.DOWN))
                .with(WATERLOGGED, world.getFluidState(pos).getFluid() == Fluids.WATER);
    }

    @Override
    public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
        if (state.get(WATERLOGGED)) {
            world.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        BooleanProperty property = FACING_TO_PROPERTY_MAP.get(facing);
        boolean connected = this.canConnectTo(world, facingPos, facing.getOpposite());
        return state.with(property, connected);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }

    private boolean canConnectAlong(IBlockReader world, BlockPos pos, Direction direction) {
        BlockPos adjacentPos = pos.offset(direction);
        BlockState adjacentState = world.getBlockState(adjacentPos);
        return this.canConnectTo(adjacentState, world, adjacentPos, direction);
    }

    private boolean canConnectTo(IBlockReader world, BlockPos pos, Direction direction) {
        return this.canConnectTo(world.getBlockState(pos), world, pos, direction);
    }

    private boolean canConnectTo(BlockState state, IBlockReader world, BlockPos pos, Direction direction) {
        return state.matchesBlock(this)
                || state.isSolidSide(world, pos, direction);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN, WATERLOGGED);
    }

    @Override
    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
        return false;
    }
}
