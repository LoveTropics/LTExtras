package com.lovetropics.extras.block;

import com.lovetropics.extras.client.particle.ExtraParticles;
import net.minecraft.block.BarrierBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.particles.IParticleData;
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

    private static final IFluidState WATERLOGGED_FLUID = new NoDripFluidState(Fluids.WATER.getStillFluidState(false));

    public WaterBarrierBlock(Properties properties) {
        super(properties);
        setDefaultState(getDefaultState().with(WATERLOGGED, true));
    }

    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Deprecated
    public IFluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? WATERLOGGED_FLUID : super.getFluidState(state);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return getDefaultState().with(WATERLOGGED, true);
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> container) {
        container.add(WATERLOGGED);
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

    private static class NoDripFluidState extends FluidState {
        NoDripFluidState(IFluidState parent) {
            super(parent.getFluid(), parent.getValues());
        }

        @Nullable
        @Override
        public IParticleData getDripParticleData() {
            return null;
        }
    }
}
