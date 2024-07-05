package com.lovetropics.extras.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EndRodBlock;
import net.minecraft.world.level.block.state.BlockState;

public class BoringEndRodBlock extends EndRodBlock {
    public BoringEndRodBlock(Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<EndRodBlock> codec() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        //It's boring, so don't animate!
    }
}
