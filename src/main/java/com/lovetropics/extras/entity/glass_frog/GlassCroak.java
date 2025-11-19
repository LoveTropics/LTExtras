package com.lovetropics.extras.entity.glass_frog;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.animal.frog.Frog;

public class GlassCroak extends Behavior<GlassFrog> {
    private static final int CROAK_TICKS = 60;
    private static final int TIME_OUT_DURATION = 100;
    private int croakCounter;

    public GlassCroak() {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT), 100);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, GlassFrog glassFrog) {
        return glassFrog.getPose() == Pose.STANDING;
    }

    @Override
    protected boolean canStillUse(ServerLevel level, GlassFrog glassFrog, long gameTime) {
        return this.croakCounter < 60;
    }

    @Override
    protected void start(ServerLevel level, GlassFrog glassFrog, long gameTime) {
        if (!glassFrog.isInLiquid()) {
            glassFrog.setPose(Pose.CROAKING);
            this.croakCounter = 0;
        }
    }

    @Override
    protected void stop(ServerLevel level, GlassFrog glassFrog, long gameTime) {
        glassFrog.setPose(Pose.STANDING);
    }

    @Override
    protected void tick(ServerLevel level, GlassFrog glassFrog, long gameTime) {
        this.croakCounter++;
    }
}
