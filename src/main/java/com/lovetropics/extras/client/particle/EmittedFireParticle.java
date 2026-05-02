package com.lovetropics.extras.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class EmittedFireParticle extends EmittedRaisingParticle {

    EmittedFireParticle(ClientLevel world, double x, double y, double z, SpriteSet sprites) {
        super(world, x, y, z, sprites);
    }

    @Override
    public void move(double pX, double pY, double pZ) {
        setBoundingBox(getBoundingBox().move(pX, pY, pZ));
        setLocationFromBoundingbox();
    }

    @Override
    public float getQuadSize(float pScaleFactor) {
        float f = ((float) age + pScaleFactor) / (float) lifetime;
        return quadSize * (1.0F - f * f * 0.5F);
    }

    @Override
    public int getLightCoords(float pPartialTick) {
        float f = ((float) age + pPartialTick) / (float) lifetime;
        f = Mth.clamp(f, 0.0F, 1.0F);
        int i = super.getLightCoords(pPartialTick);
        int j = i & 255;
        int k = i >> 16 & 255;
        j += (int) (f * 15.0F * 16.0F);
        if (j > 240) {
            j = 240;
        }

        return j | k << 16;
    }

    record Factory(SpriteSet spriteSet) implements ParticleProvider<SimpleParticleType> {

        @Override
        public Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random) {
            return new EmittedFireParticle(level, x, y, z, spriteSet);
        }
    }
}
