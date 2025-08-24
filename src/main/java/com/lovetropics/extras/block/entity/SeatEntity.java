package com.lovetropics.extras.block.entity;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PlayerRideable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class SeatEntity extends Entity implements PlayerRideable {
    public SeatEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void removePassenger(Entity passenger) {
        super.removePassenger(passenger);
        remove(RemovalReason.DISCARDED);
    }

	@Override
	public boolean hurtServer(ServerLevel level, DamageSource damageSource, float amount) {
		return false;
	}

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

	@Override
	protected void readAdditionalSaveData(ValueInput input) {
	}

	@Override
	protected void addAdditionalSaveData(ValueOutput output) {
	}
}
