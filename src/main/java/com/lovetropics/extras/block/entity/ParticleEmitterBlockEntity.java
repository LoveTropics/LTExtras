package com.lovetropics.extras.block.entity;

import com.lovetropics.extras.client.particle.ExtraParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ParticleEmitterBlockEntity extends BlockEntity {
    // mode 0: crit hit particles
    // mode 1: fire particles
    private int mode = 0;

    public ParticleEmitterBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pType, pWorldPosition, pBlockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ParticleEmitterBlockEntity be) {
        if (level instanceof ServerLevel slevel) {
            if (slevel.hasNeighborSignal(pos)) {
                return;
            }

            // Choose between types
            int time;
            SimpleParticleType particle;
            int particleCount;
            if (be.mode == 1) {
                time = (int) (level.getGameTime() + 40);
                particle = ExtraParticles.EMITTED_FIRE_PARTICLE.get();
                particleCount = 8;
            } else {
                time = (int) (level.getGameTime());
                particle = ExtraParticles.EMITTED_PARTICLE.get();
                particleCount = 5;
            }

            int tickMod = time % 80;
            if (tickMod >= 0 && tickMod < 20) {
                slevel.sendParticles(particle, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, particleCount, 0.05, 0, 0.05, 1);
            }
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        mode = tag.getInt("Mode");
    }

    @Override
    protected void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);

        compound.putInt("Mode", mode);
    }
}
