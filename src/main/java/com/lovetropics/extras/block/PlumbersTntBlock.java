package com.lovetropics.extras.block;

import com.lovetropics.extras.entity.PrimedPlumbersTnt;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import javax.annotation.Nullable;

public class PlumbersTntBlock extends TntBlock {

    public PlumbersTntBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void wasExploded(ServerLevel level, BlockPos pos, Explosion explosion) {
        PrimedPlumbersTnt primedtnt = PrimedPlumbersTnt.create(level, (double)pos.getX() + (double)0.5F, pos.getY(), (double)pos.getZ() + (double)0.5F, explosion.getIndirectSourceEntity());
        final int fuse = primedtnt.getFuse();
        primedtnt.setFuse((short)(level.getRandom().nextInt(fuse / 4) + fuse / 8));
        level.addFreshEntity(primedtnt);
    }

    @Override
    public boolean onCaughtFire(BlockState state, Level world, BlockPos pos, @Nullable Direction face, @Nullable LivingEntity igniter) {
        return prime(world, pos, igniter);
    }

    private static boolean prime(Level level, BlockPos pos, @Nullable LivingEntity entity) {
        if (level instanceof ServerLevel) {
            PrimedPlumbersTnt primedtnt = PrimedPlumbersTnt.create(level, (double)pos.getX() + (double)0.5F, pos.getY(), (double)pos.getZ() + (double)0.5F, entity);
            level.addFreshEntity(primedtnt);
            level.playSound(null, primedtnt.getX(), primedtnt.getY(), primedtnt.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(entity, GameEvent.PRIME_FUSE, pos);
            return true;
        }

        return false;
    }
}
