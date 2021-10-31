package com.lovetropics.extras.block;

import com.lovetropics.extras.ExtendedFluidState;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class WaterBarrierBlock extends CustomBarrierBlock implements IWaterLoggable {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final FluidState WATERLOGGED_FLUID = createNoDripState(Fluids.WATER.getStillFluidState(false));

    private static FluidState createNoDripState(FluidState parent) {
        FluidState state = new FluidState(parent.getFluid(), parent.getValues(), parent.mapCodec);
        ((ExtendedFluidState) (Object) state).setNoDripParticles();
        return state;
    }

    public WaterBarrierBlock(Properties properties) {
        super(properties);
        this.setDefaultState(this.getDefaultState().with(WATERLOGGED, true));
    }

    @Override
    @Deprecated
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? WATERLOGGED_FLUID : super.getFluidState(state);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(WATERLOGGED, true);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> container) {
        container.add(WATERLOGGED);
    }

    @Override
    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid) {
        // Copied from super
        this.getBlock().onBlockHarvested(world, pos, state, player);
        // Changed to set air instead of the fluid state
        return world.setBlockState(pos, Blocks.AIR.getDefaultState(), world.isRemote ? 11 : 3);
    }
}
