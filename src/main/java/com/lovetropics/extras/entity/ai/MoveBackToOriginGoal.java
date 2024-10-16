package com.lovetropics.extras.entity.ai;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class MoveBackToOriginGoal extends RandomStrollGoal {
	private final Vec3 vec;
	private final int rangeSq;

	public MoveBackToOriginGoal(PathfinderMob creatureIn, double speedIn, Vec3 vec, int range) {
		super(creatureIn, speedIn);
		this.vec = vec;
		rangeSq = range * range;
	}

	@Override
	public boolean canUse() {
		if (mob.position().distanceToSqr(vec) > rangeSq) {
			Vec3 vec = getPosition();

			if (vec == null) {
				return false;
			}

			// Store position- we don't call super so we must do this ourselves
			wantedX = vec.x();
			wantedY = vec.y();
			wantedZ = vec.z();

			return true;
		}

		return false;
	}

	@Nullable
	@Override
	protected Vec3 getPosition() {
		return vec;
	}
}
