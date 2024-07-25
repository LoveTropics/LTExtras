package com.lovetropics.extras.entity.ravekoa;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class RaveKoaEntityDance2 extends RaveKoaEntity {

    public RaveKoaEntityDance2(EntityType<? extends RaveKoaEntityDance2> type, Level world) {
        super(type, world);
    }

    @Override
    public void tick() {
        raveAnimationStateDance2.startIfStopped(tickCount);
        super.tick();
    }
}
