package com.lovetropics.extras.block;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrierBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public abstract class CustomBarrierBlock extends BarrierBlock {
    public CustomBarrierBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        Client.animateTick(world, pos, this, createParticle());
    }

    protected ParticleOptions createParticle() {
        return new BlockParticleOption(ParticleTypes.BLOCK_MARKER, Blocks.BARRIER.defaultBlockState());
    }

    private static class Client {
        private static void animateTick(Level world, BlockPos pos, Block block, ParticleOptions particle) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) {
                return;
            }

            if (isHoldingBarrier(player, block.asItem())) {
                world.addParticle(particle, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0.0, 0.0, 0.0);
            }
        }

        private static boolean isHoldingBarrier(Player player, Item item) {
            return player.isHolding(item);
        }
    }
}
