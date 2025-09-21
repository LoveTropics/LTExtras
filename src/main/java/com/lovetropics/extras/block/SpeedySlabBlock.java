package com.lovetropics.extras.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;

public class SpeedySlabBlock extends SlabBlock {
    public SpeedySlabBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        SpeedyBlock.applySpeedy(entity);
        super.stepOn(level, pos, state, entity);
    }
}
