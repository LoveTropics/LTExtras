package com.lovetropics.extras.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public final class RopeBlock extends Block {
    private static final VoxelShape SHAPE = Block.makeCuboidShape(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);

    public static final BooleanProperty KNOT = BooleanProperty.create("knot");

    public RopeBlock(Properties properties) {
        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(KNOT, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
        if (facing == Direction.UP && !this.canHangFrom(world, facingPos, facingState)) {
            return Blocks.AIR.getDefaultState();
        }

        if (facing == Direction.DOWN) {
            return state.with(KNOT, !facingState.matchesBlock(this));
        }

        return state;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getPos();

        if (this.canHangAt(world, pos)) {
            return this.getDefaultState().with(KNOT, this.isKnottedAt(world, pos));
        } else {
            return null;
        }
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos) {
        return this.canHangAt(world, pos);
    }

    private boolean canHangAt(IWorldReader world, BlockPos pos) {
        BlockPos attachPos = pos.up();
        BlockState attachState = world.getBlockState(attachPos);
        return this.canHangFrom(world, attachPos, attachState);
    }

    private boolean canHangFrom(IWorldReader world, BlockPos attachPos, BlockState attachState) {
        return attachState.matchesBlock(this) ||
                attachState.isSolidSide(world, attachPos, Direction.DOWN) ||
                attachState.isIn(BlockTags.LEAVES) ||
                attachState.isIn(BlockTags.FENCES) ||
                attachState.isIn(BlockTags.WALLS);
    }

    private boolean isKnottedAt(IWorldReader world, BlockPos pos) {
        return !world.getBlockState(pos.down()).matchesBlock(this);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(KNOT);
    }
}
