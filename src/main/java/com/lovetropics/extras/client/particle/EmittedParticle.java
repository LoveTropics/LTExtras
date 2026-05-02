package com.lovetropics.extras.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

public class EmittedParticle extends SingleQuadParticle {

    EmittedParticle(ClientLevel world, double x, double y, double z, SpriteSet sprites) {
        super(world, x, y, z, sprites.first());
        lifetime = 80;
        gravity = 0.5f;
        yd = 0.75f;
        xd = (Math.random() - Math.random()) * 0.05;
        zd = (Math.random() - Math.random()) * 0.05;
        setSize(0.5f, 0.5f);

        float f = (float) (Math.random() * (double) 0.3F + (double) 0.6F);
        rCol = f;
        gCol = f;
        bCol = f;

    }

    @Override
    protected Layer getLayer() {
        return Layer.OPAQUE;
    }

    record Factory(SpriteSet spriteSet) implements ParticleProvider<SimpleParticleType> {

        @Override
        public Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random) {
            return new EmittedParticle(level, x, y, z, spriteSet);
        }
    }
}
