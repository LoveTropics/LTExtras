package com.lovetropics.extras.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class TeleportPadBlockEntity extends BlockEntity {
    private static final String TAG_TARGET_POS = "target";

    @Nullable
    private Vec3 targetPos;

    public TeleportPadBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public void updateTarget(Vec3 targetPos) {
        this.targetPos = targetPos;
    }

    @Nullable
    public Vec3 getTargetPos() {
        return targetPos;
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        targetPos = input.read(TAG_TARGET_POS, Vec3.CODEC).orElse(null);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.storeNullable(TAG_TARGET_POS, Vec3.CODEC, targetPos);
    }
}
