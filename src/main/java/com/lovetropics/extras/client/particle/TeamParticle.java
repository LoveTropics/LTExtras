package com.lovetropics.extras.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.HeartParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

public class TeamParticle extends HeartParticle {

    TeamParticle(ClientLevel world, double x, double y, double z, SpriteSet sprites) {
        super(world, x, y, z, sprites.first());
    }

    record TeamFactory(SpriteSet spriteSet) implements ParticleProvider<SimpleParticleType> {

        @Override
        public Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random) {
            return new TeamParticle(level, x, y, z, spriteSet);
        }
    }

}
