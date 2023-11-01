package com.lovetropics.extras.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

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
