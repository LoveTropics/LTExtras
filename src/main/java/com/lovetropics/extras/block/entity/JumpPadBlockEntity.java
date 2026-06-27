package com.lovetropics.extras.block.entity;

import com.lovetropics.extras.block.JumpPadBlock;
import com.lovetropics.extras.block.TrajectorySolver;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

import org.jspecify.annotations.Nullable;

public class JumpPadBlockEntity extends BlockEntity {
    private static final String TAG_TARGET_POS = "target";
    private static final String TAG_ANGLE = "angle";
    private static final String TAG_MAX_VELOCITY = "max_velocity";

    private static final float DEFAULT_ANGLE = 45.0f;
    private static final float DEFAULT_MAX_VELOCITY = 10.0f;

    private @Nullable Vec3 targetPos;
    private float angle = DEFAULT_ANGLE;
    private float maxVelocity = DEFAULT_MAX_VELOCITY;

    private @Nullable Vec3 launchVelocity;

    public JumpPadBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public void updateTarget(Vec3 targetPos) {
        this.targetPos = targetPos;
        launchVelocity = null;
        BlockPos blockPos = getBlockPos();
        getLevel().setBlockAndUpdate(blockPos, getBlockState().setValue(JumpPadBlock.FACING, getDirectionToTarget(targetPos, blockPos)));
    }

    private static Direction getDirectionToTarget(Vec3 targetPos, BlockPos blockPos) {
        double deltaX = targetPos.x - blockPos.getX() - 0.5;
        double deltaZ = targetPos.z - blockPos.getZ() - 0.5;
        if (Math.abs(deltaX) < 0.5 && Math.abs(deltaZ) < 0.5) {
            return Direction.UP;
        }
        if (Math.abs(deltaX) > Math.abs(deltaZ)) {
            return deltaX > 0.0 ? Direction.EAST : Direction.WEST;
        } else {
            return deltaZ > 0.0 ? Direction.SOUTH : Direction.NORTH;
        }
    }

    public Vec3 getLaunchVelocity() {
        if (launchVelocity == null) {
            launchVelocity = computeLaunchVelocity();
        }
        return launchVelocity;
    }

    private Vec3 computeLaunchVelocity() {
        if (targetPos == null) {
            return Vec3.ZERO;
        }
        Vec3 origin = Vec3.atCenterOf(getBlockPos());
        if (getBlockState().getValue(JumpPadBlock.HALF) == Half.TOP) {
            origin = origin.add(0.0, 0.5, 0.0);
        }
        return TrajectorySolver.STANDARD.solveVelocity(origin, targetPos, angle * Mth.DEG_TO_RAD, true, maxVelocity);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        targetPos = input.read(TAG_TARGET_POS, Vec3.CODEC).orElse(null);
        angle = input.getFloatOr(TAG_ANGLE, DEFAULT_ANGLE);
        maxVelocity = input.getFloatOr(TAG_MAX_VELOCITY, DEFAULT_MAX_VELOCITY);
        launchVelocity = null;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.storeNullable(TAG_TARGET_POS, Vec3.CODEC, targetPos);
        output.putFloat(TAG_ANGLE, angle);
        output.putFloat(TAG_MAX_VELOCITY, maxVelocity);
    }
}
