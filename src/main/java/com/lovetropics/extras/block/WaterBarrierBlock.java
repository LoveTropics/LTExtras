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

import net.minecraft.block.AbstractBlock.Properties;

public class WaterBarrierBlock extends CustomBarrierBlock implements IWaterLoggable {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final FluidState WATERLOGGED_FLUID = createNoDripState(Fluids.WATER.getSource(false));

    private static FluidState createNoDripState(FluidState parent) {
        FluidState state = new FluidState(parent.getType(), parent.getValues(), parent.propertiesCodec);
        ((ExtendedFluidState) (Object) state).setNoDripParticles();
        return state;
    }

    public WaterBarrierBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, true));
    }

    @Override
    @Deprecated
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? WATERLOGGED_FLUID : super.getFluidState(state);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState().setValue(WATERLOGGED, true);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> container) {
        container.add(WATERLOGGED);
    }

    @Override
    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid) {
        // Copied from super
        this.getBlock().playerWillDestroy(world, pos, state, player);
        // Changed to set air instead of the fluid state
        return world.setBlock(pos, Blocks.AIR.defaultBlockState(), world.isClientSide ? 11 : 3);
    }
}
