package com.lovetropics.extras.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;

public class EmittedRaisingParticle extends TextureSheetParticle {
    protected EmittedRaisingParticle(ClientLevel world, double x, double y, double z, SpriteSet sprites) {
        super(world, x, y, z);
        this.lifetime = 40 + (int) (Math.random() * 5);
        this.yd = 0.25f;
        this.xd = (Math.random() - Math.random()) * 0.05;
        this.zd = (Math.random() - Math.random()) * 0.05;
        this.setSize(0.5f, 0.5f);

        pickSprite(sprites);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }
}
