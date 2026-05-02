package com.lovetropics.extras.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

public class EmittedHeartsParticle extends EmittedRaisingParticle {

    EmittedHeartsParticle(ClientLevel world, double x, double y, double z, SpriteSet sprites) {
        super(world, x, y, z, sprites);
        setSize(0.75f, 0.75f);
    }

    record Factory(SpriteSet sprites) implements ParticleProvider<SimpleParticleType> {

        @Override
        public Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random) {
            return new EmittedHeartsParticle(level, x, y, z, sprites);
        }
    }
}
