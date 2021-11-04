package com.lovetropics.extras.entity.ai;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;

public class MoveBackToOriginGoal extends RandomWalkingGoal {
    private final Vector3d vec;
    private final int rangeSq;

    public MoveBackToOriginGoal(CreatureEntity creatureIn, double speedIn, Vector3d vec, int range) {
        super(creatureIn, speedIn);
        this.vec = vec;
        this.rangeSq = range * range;
    }

    @Override
    public boolean shouldExecute() {
        if (this.creature.getPositionVec().squareDistanceTo(this.vec) > this.rangeSq) {
            Vector3d vec = this.getPosition();

            if (vec == null) {
                return false;
            }

            // Store position- we don't call super so we must do this ourselves
            this.x = vec.getX();
            this.y = vec.getY();
            this.z = vec.getZ();

            return true;
        }

        return false;
    }

    @Nullable
    @Override
    protected Vector3d getPosition() {
        return this.vec;
    }
}
