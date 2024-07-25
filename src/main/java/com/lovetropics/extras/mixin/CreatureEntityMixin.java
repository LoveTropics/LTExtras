package com.lovetropics.extras.mixin;

import com.lovetropics.extras.block.entity.MobControllerBlockEntity;
import com.lovetropics.extras.entity.ExtendedCreatureEntity;
import com.lovetropics.extras.entity.ai.MoveBackToOriginGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PathfinderMob.class)
public abstract class CreatureEntityMixin extends Mob implements ExtendedCreatureEntity {
	// home
	private boolean theresNoPlaceLikeHome;
	private Vec3 homePos;
	private int homeRange;

	// external controller

	protected CreatureEntityMixin(EntityType<? extends Mob> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag nbt) {
		super.readAdditionalSaveData(nbt);

		if (nbt.contains("TheresNoPlaceLikeHome")) {
			theresNoPlaceLikeHome = nbt.getBoolean("TheresNoPlaceLikeHome");

			if (nbt.contains("HomePos")) {
				ListTag posTag = nbt.getList("HomePos", Tag.TAG_DOUBLE);
				homePos = new Vec3(posTag.getDouble(0), posTag.getDouble(1), posTag.getDouble(2));
			} else {
				// Spawn egg or no recorded home- just grab the current position to have something to work with
				homePos = position();
			}
			// In blocks
			homeRange = nbt.contains("HomeRange") ? nbt.getInt("HomeRange") : 20;

			if (theresNoPlaceLikeHome) {
				goalSelector.addGoal(0, new MoveBackToOriginGoal((PathfinderMob) (Object) this, 1.0, homePos, homeRange));
			}
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag nbt) {
		super.addAdditionalSaveData(nbt);

		// Don't pollute! Only write if it'll be used
		if (theresNoPlaceLikeHome) {
			nbt.putBoolean("TheresNoPlaceLikeHome", theresNoPlaceLikeHome);
			nbt.putInt("HomeRange", homeRange);

			// vec3d -> nbt
			ListTag pos = new ListTag();
			pos.add(0, DoubleTag.valueOf(homePos.x()));
			pos.add(1, DoubleTag.valueOf(homePos.y()));
			pos.add(2, DoubleTag.valueOf(homePos.z()));

			nbt.put("HomePos", pos);
		}
	}

	@Override
	public void linkToBlockEntity(MobControllerBlockEntity controller) {
		BlockPos pos = controller.getBlockPos();
		theresNoPlaceLikeHome = true;
		homePos = new Vec3(pos.getX(), pos.getY(), pos.getZ());
		homeRange = 32; // static for now
	}
}
