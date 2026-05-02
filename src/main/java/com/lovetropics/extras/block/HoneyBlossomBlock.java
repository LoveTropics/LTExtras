package com.lovetropics.extras.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SporeBlossomBlock;
import net.minecraft.world.level.block.state.BlockState;

public class HoneyBlossomBlock extends SporeBlossomBlock {
    public HoneyBlossomBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        double d0 = i + random.nextDouble();
        double d1 = j + 0.7;
        double d2 = k + random.nextDouble();
        level.addParticle(ParticleTypes.DRIPPING_HONEY, d0, d1, d2, 0.0, 0.0, 0.0);
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (int l = 0; l < 14; l++) {
            mutable.set(i + Mth.nextInt(random, -10, 10), j - random.nextInt(10), k + Mth.nextInt(random, -10, 10));
            BlockState blockstate = level.getBlockState(mutable);
            if (!blockstate.isCollisionShapeFullBlock(level, mutable)) {
                level.addParticle(
                        ParticleTypes.FALLING_HONEY,
                        mutable.getX() + random.nextDouble(),
                        mutable.getY() + random.nextDouble(),
                        mutable.getZ() + random.nextDouble(),
                        0.0,
                        0.0,
                        0.0
                );
            }
        }
    }
}
