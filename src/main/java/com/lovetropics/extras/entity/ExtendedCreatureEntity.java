package com.lovetropics.extras.entity;

import com.lovetropics.extras.block.entity.MobControllerBlockEntity;
import net.minecraft.nbt.CompoundNBT;

public interface ExtendedCreatureEntity {
    void linkToBlockEntity(MobControllerBlockEntity controller);
}
