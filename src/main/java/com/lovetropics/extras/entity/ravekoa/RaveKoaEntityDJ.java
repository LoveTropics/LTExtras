package com.lovetropics.extras.entity.ravekoa;

import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;

public class RaveKoaEntityDJ extends RaveKoaEntity {

    public RaveKoaEntityDJ(EntityType<? extends RaveKoaEntityDJ> type, Level world) {
        super(type, world);
    }

    @Override
    public void tick() {
        raveAnimationStateDJ.startIfStopped(this.tickCount);
        super.tick();
    }
}
