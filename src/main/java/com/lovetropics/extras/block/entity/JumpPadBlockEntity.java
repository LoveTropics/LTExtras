package com.lovetropics.extras.block.entity;

import com.lovetropics.extras.block.JumpPadBlock;
import com.lovetropics.extras.block.TrajectorySolver;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class JumpPadBlockEntity extends BlockEntity {
	private static final String TAG_TARGET_POS = "target";
	private static final String TAG_ANGLE = "angle";
	private static final String TAG_MAX_VELOCITY = "max_velocity";

	private static final float DEFAULT_ANGLE = 45.0f;
	private static final float DEFAULT_MAX_VELOCITY = 10.0f;

	@Nullable
	private Vec3 targetPos;
	private float angle = DEFAULT_ANGLE;
	private float maxVelocity = DEFAULT_MAX_VELOCITY;

	public JumpPadBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
		super(type, pos, blockState);
	}

	public void setTarget(Vec3 targetPos) {
		this.targetPos = targetPos;
		BlockPos blockPos = getBlockPos();
		getLevel().setBlockAndUpdate(blockPos, getBlockState().setValue(JumpPadBlock.FACING, getDirectionToTarget(targetPos, blockPos)));
	}

	private static Direction getDirectionToTarget(Vec3 targetPos, BlockPos blockPos) {
		double deltaX = targetPos.x - blockPos.getX() + 0.5;
		double deltaZ = targetPos.z - blockPos.getZ() + 0.5;
		if (Mth.equal(deltaX, 0.0) && Mth.equal(deltaZ, 0.0)) {
			return Direction.UP;
		}
		if (Math.abs(deltaX) > Math.abs(deltaZ)) {
			return deltaX > 0.0 ? Direction.EAST : Direction.WEST;
		} else {
			return deltaZ > 0.0 ? Direction.SOUTH : Direction.NORTH;
		}
	}

	public Vec3 computeLaunchVelocity(Entity entity) {
		if (targetPos == null) {
			return Vec3.ZERO;
		}
		return TrajectorySolver.STANDARD.solveVelocity(entity.position(), targetPos, angle * Mth.DEG_TO_RAD, true, maxVelocity);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		if (tag.contains(TAG_TARGET_POS)) {
			Vec3.CODEC.parse(NbtOps.INSTANCE, tag.get(TAG_TARGET_POS)).ifSuccess(pos -> targetPos = pos);
		} else {
			targetPos = null;
		}
		angle = tag.contains(TAG_ANGLE, Tag.TAG_FLOAT) ? tag.getFloat(TAG_ANGLE) : DEFAULT_ANGLE;
		maxVelocity = tag.contains(TAG_MAX_VELOCITY, Tag.TAG_FLOAT) ? tag.getFloat(TAG_MAX_VELOCITY) : DEFAULT_MAX_VELOCITY;
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		if (targetPos != null) {
			tag.put(TAG_TARGET_POS, Vec3.CODEC.encodeStart(NbtOps.INSTANCE, targetPos).getOrThrow());
		}
		tag.putFloat(TAG_ANGLE, angle);
		tag.putFloat(TAG_MAX_VELOCITY, maxVelocity);
	}
}
