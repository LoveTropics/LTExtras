package com.lovetropics.extras.block;

import com.lovetropics.extras.ExtendedFluidState;
import com.lovetropics.extras.client.particle.ExtraParticles;
import net.minecraft.block.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Random;

public class WaterBarrierBlock extends BarrierBlock implements IWaterLoggable {

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
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
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

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, World world, BlockPos pos, Random random) {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        if (this.isHoldingBarrier(player)) {
            world.addParticle(ExtraParticles.WATER_BARRIER.get(), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0.0, 0.0, 0.0);
        }
    }

    private boolean isHoldingBarrier(PlayerEntity player) {
        Item item = this.asItem();
        return player.getHeldItemMainhand().getItem() == item
                || player.getHeldItemOffhand().getItem() == item;
    }
}
