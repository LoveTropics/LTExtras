package com.lovetropics.extras.entity.vfx;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Random;

public class PartyBeamEntity extends EndCrystal {
    // Seems like there's no vector3 encoder in EntityDataSerializers.
    // I'm sorry.
    private static final EntityDataAccessor<Optional<BlockPos>> DATA_COLOR = SynchedEntityData.defineId(PartyBeamEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    private BlockPos targetPos = null;
    public PartyBeamEntity(EntityType<? extends EndCrystal> p_31037_, Level p_31038_) {
        super(p_31037_, p_31038_);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level instanceof ServerLevel) {
            if (targetPos != null && this.level.getGameTime() % 100 == 0) {
                RandomSource random = this.random;
                random.setSeed(this.level.getGameTime());

                int ax = targetPos.getX() + (random.nextInt(5) - random.nextInt(5));
                int az = targetPos.getZ() + (random.nextInt(5) - random.nextInt(5));

                int packed = Mth.hsvToRgb(random.nextFloat(), 0.8f, 0.8F);
                int r = (packed >> 16) & 0xFF;
                int g = (packed >> 8) & 0xFF;
                int b = packed & 0xFF;

                this.setColor(new BlockPos(r, g, b));

                this.setBeamTarget(new BlockPos(ax, targetPos.getY(), az));
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        if (targetPos != null) {
            pCompound.put("TargetPos", NbtUtils.writeBlockPos(targetPos));
        }

        if (this.getColor() != null) {
            pCompound.put("TargetColor", NbtUtils.writeBlockPos(this.getColor()));
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        if (pCompound.contains("TargetPos", 10)) {
            targetPos = NbtUtils.readBlockPos(pCompound.getCompound("TargetPos"));
        }

        if (pCompound.contains("TargetColor", 10)) {
            this.setColor(NbtUtils.readBlockPos(pCompound.getCompound("TargetColor")));
        }
    }

    public void setColor(@Nullable BlockPos rgbColorVector) {
        this.getEntityData().set(DATA_COLOR, Optional.ofNullable(rgbColorVector));
    }

    @Nullable
    public BlockPos getColor() {
        return this.getEntityData().get(DATA_COLOR).orElse(null);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_COLOR, Optional.empty());
    }

    public boolean hurt(DamageSource pSource, float pAmount) {
        if (this.isInvulnerableTo(pSource)) {
            return false;
        } else if (pSource.getEntity() instanceof EnderDragon) {
            return false;
        } else {
            if (!this.isRemoved() && !this.level.isClientSide) {
                this.remove(Entity.RemovalReason.KILLED);

                // No explosion

                this.kill();
            }

            return true;
        }
    }
}
