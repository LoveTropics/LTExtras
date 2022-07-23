package com.lovetropics.extras.block;

import com.lovetropics.extras.ExtendedFluidState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import javax.annotation.Nullable;

public class WaterBarrierBlock extends CustomBarrierBlock implements SimpleWaterloggedBlock {

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
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(WATERLOGGED, true);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> container) {
        container.add(WATERLOGGED);
    }

    @Override
    public boolean canPlaceLiquid(BlockGetter pLevel, BlockPos pPos, BlockState pState, Fluid pFluid) {
        return false;
    }

    @Override
    public boolean placeLiquid(LevelAccessor pLevel, BlockPos pPos, BlockState pState, FluidState pFluidState) {
        return false;
    }

    @Override
    public ItemStack pickupBlock(LevelAccessor pLevel, BlockPos pPos, BlockState pState) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean onDestroyedByPlayer(final BlockState state, final Level level, final BlockPos pos, final Player player, final boolean willHarvest, final FluidState fluid) {
        // Copied from super
        this.asBlock().playerWillDestroy(level, pos, state, player);
        // Changed to set air instead of the fluid state
        return level.setBlock(pos, Blocks.AIR.defaultBlockState(), level.isClientSide ? 11 : 3);
    }
}
