package com.lovetropics.extras.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class RaveKoaEntityDance1 extends RaveKoaEntity {

    public RaveKoaEntityDance1(EntityType<? extends RaveKoaEntityDance1> type, Level world) {
        super(type, world);
    }

    @Override
    public void tick() {
        raveAnimationStateDance1.startIfStopped(this.tickCount);
        super.tick();
    }
}
