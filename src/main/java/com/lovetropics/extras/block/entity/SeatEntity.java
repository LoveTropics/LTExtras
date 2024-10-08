package com.lovetropics.extras.block.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PlayerRideable;
import net.minecraft.world.level.Level;

public class SeatEntity extends Entity implements PlayerRideable {


    public SeatEntity(final EntityType<?> entityType, final Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(final SynchedEntityData.Builder builder) {

    }

    @Override
    protected void readAdditionalSaveData(final CompoundTag compoundTag) {

    }

    @Override
    protected void addAdditionalSaveData(final CompoundTag compoundTag) {

    }
}
