package com.lovetropics.extras.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.DripParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.material.Fluids;

public class HoneyShortFallParticle extends DripParticle {

    public HoneyShortFallParticle(ClientLevel level, double x, double y, double z, SpriteSet sprite) {
        super(level, x, y, z, Fluids.WATER, sprite.first());
        this.gravity = 0.01F;
        this.lifetime = 8;
        this.setColor(0.582F, 0.448F, 0.082F);
    }

    protected void postMoveUpdate() {
        if (this.onGround) {
            this.remove();
            float f = Mth.randomBetween(this.random, 0.3F, 1.0F);
            if (this.random.nextFloat() < 0.05f) {
                this.level.playLocalSound(this.x, this.y, this.z, SoundEvents.BEEHIVE_DRIP, SoundSource.BLOCKS, f, 1.0F, false);
            }
        }
    }

    record Factory(SpriteSet spriteSet) implements ParticleProvider<SimpleParticleType> {

        @Override
        public Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random) {
            return new HoneyShortFallParticle(level, x, y, z, spriteSet);
        }
    }
}
