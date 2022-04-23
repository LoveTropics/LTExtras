package com.lovetropics.extras.block;

import net.minecraft.block.BarrierBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

import net.minecraft.block.AbstractBlock.Properties;

public abstract class CustomBarrierBlock extends BarrierBlock {
    public CustomBarrierBlock(Properties properties) {
        super(properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, World world, BlockPos pos, Random random) {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        if (this.isHoldingBarrier(player)) {
            world.addParticle(ParticleTypes.BARRIER, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0.0, 0.0, 0.0);
        }
    }

    private boolean isHoldingBarrier(PlayerEntity player) {
        Item item = this.asItem();
        return player.getMainHandItem().getItem() == item
                || player.getOffhandItem().getItem() == item;
    }
}
