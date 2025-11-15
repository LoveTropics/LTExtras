package com.lovetropics.extras.entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

/**
 * Freshwater dolphin found in Amazon rivers.
 * <p>
 * Mouth code yoinked from Tropicraft like a boss.
 */
public class AmazonRiverDolphin extends Dolphin {
    private static final EntityDataAccessor<Boolean> MOUTH_OPEN = SynchedEntityData.defineId(AmazonRiverDolphin.class, EntityDataSerializers.BOOLEAN);

    public AmazonRiverDolphin(EntityType<? extends AmazonRiverDolphin> type, Level level) {
        super(type, level);
    }

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(MOUTH_OPEN, false);
    }

    @Nullable
    public Dolphin getBreedOffspring(ServerLevel level, AgeableMob mob) {
        return ExtraEntities.AMAZON_RIVER_DOLPHIN.create(level, EntitySpawnReason.BREEDING);
    }

    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            if (this.ambientSoundTime < -(this.getAmbientSoundInterval() - 20)) {
                if (this.tickCount % 3 > 1) {
                    if (!this.getMouthOpen()) {
                        this.setMouthOpen(true);
                    }
                } else if (this.getMouthOpen()) {
                    this.setMouthOpen(false);
                }
            } else if (this.getMouthOpen()) {
                this.setMouthOpen(false);
            }
        }
    }

    public void setMouthOpen(boolean b) {
        this.getEntityData().set(MOUTH_OPEN, b);
    }

    public boolean getMouthOpen() {
        return this.getEntityData().get(MOUTH_OPEN);
    }
}
