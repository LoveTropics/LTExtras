package com.lovetropics.extras.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class WordBoxBlockEntity extends BlockEntity {


    public WordBoxBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    protected void saveAdditional(ValueOutput valueOutput) {
        super.saveAdditional(valueOutput);

    }

    protected void loadAdditional(ValueInput valueInput) {
        super.loadAdditional(valueInput);

    }

    public Component getText() {
        return components().getOrDefault(DataComponents.CUSTOM_NAME, Component.literal(""));
    }
}
