package com.lovetropics.extras.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
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

import net.minecraft.block.AbstractBlock.Properties;

public final class RopeBlock extends Block implements IWaterLoggable {
    private static final VoxelShape SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);

    public static final BooleanProperty KNOT = BooleanProperty.create("knot");
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public RopeBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(KNOT, false).setValue(WATERLOGGED, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
        if (facing == Direction.UP && !this.canHangFrom(world, facingPos, facingState)) {
            return Blocks.AIR.defaultBlockState();
        }

        if (state.getValue(WATERLOGGED)) {
            world.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }

        if (facing == Direction.DOWN) {
            return state.setValue(KNOT, !facingState.is(this));
        }

        return state;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        World world = context.getLevel();
        BlockPos pos = context.getClickedPos();

        if (this.canHangAt(world, pos)) {
            return this.defaultBlockState()
                    .setValue(KNOT, this.isKnottedAt(world, pos))
                    .setValue(WATERLOGGED, world.getFluidState(pos).getType() == Fluids.WATER);
        } else {
            return null;
        }
    }

    @Override
    public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos) {
        return this.canHangAt(world, pos);
    }

    private boolean canHangAt(IWorldReader world, BlockPos pos) {
        BlockPos attachPos = pos.above();
        BlockState attachState = world.getBlockState(attachPos);
        return this.canHangFrom(world, attachPos, attachState);
    }

    private boolean canHangFrom(IWorldReader world, BlockPos attachPos, BlockState attachState) {
        return attachState.is(this) ||
                Block.canSupportCenter(world, attachPos, Direction.DOWN) ||
                attachState.is(BlockTags.LEAVES);
    }

    private boolean isKnottedAt(IWorldReader world, BlockPos pos) {
        return !world.getBlockState(pos.below()).is(this);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(KNOT, WATERLOGGED);
    }
}
