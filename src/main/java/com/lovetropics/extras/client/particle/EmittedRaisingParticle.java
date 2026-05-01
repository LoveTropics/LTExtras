package com.lovetropics.extras.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;

public class EmittedRaisingParticle extends SingleQuadParticle {

    protected EmittedRaisingParticle(ClientLevel world, double x, double y, double z, SpriteSet sprites) {
        super(world, x, y, z, sprites.first());
        lifetime = 40 + (int) (Math.random() * 5);
        yd = 0.25f;
        xd = (Math.random() - Math.random()) * 0.05;
        zd = (Math.random() - Math.random()) * 0.05;
        setSize(0.5f, 0.5f);
    }

    @Override
    protected Layer getLayer() {
        return Layer.OPAQUE;
    }

}
