package com.lovetropics.extras.entity.vfx;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

public class PartyBeamEntity extends EndCrystal {
    private static final EntityDataAccessor<Vector3f> DATA_COLOR = SynchedEntityData.defineId(PartyBeamEntity.class, EntityDataSerializers.VECTOR3);
    private BlockPos targetPos = null;
    public PartyBeamEntity(EntityType<? extends EndCrystal> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            if (targetPos != null && this.level().getGameTime() % 100 == 0) {
                random.setSeed(this.level().getGameTime());

                int ax = targetPos.getX() + (random.nextInt(5) - random.nextInt(5));
                int az = targetPos.getZ() + (random.nextInt(5) - random.nextInt(5));

                int packed = Mth.hsvToRgb(random.nextFloat(), 0.8f, 0.8F);
                int r = FastColor.ARGB32.red(packed);
                int g = FastColor.ARGB32.green(packed);
                int b = FastColor.ARGB32.blue(packed);

                this.setColor(new Vector3f(r / 255.0f, g / 255.0f, b / 255.0f));

                this.setBeamTarget(new BlockPos(ax, targetPos.getY(), az));
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        if (targetPos != null) {
            tag.put("TargetPos", NbtUtils.writeBlockPos(targetPos));
        }

        if (this.getColor() != null) {
            ExtraCodecs.VECTOR3F.encodeStart(NbtOps.INSTANCE, getColor()).result().ifPresent(color -> tag.put("TargetColor", color));
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        if (tag.contains("TargetPos", Tag.TAG_COMPOUND)) {
            targetPos = NbtUtils.readBlockPos(tag.getCompound("TargetPos"));
        }

        if (tag.contains("TargetColor", Tag.TAG_COMPOUND)) {
            ExtraCodecs.VECTOR3F.parse(NbtOps.INSTANCE, tag.get("TargetColor")).result().ifPresent(this::setColor);
        }
    }

    public void setColor(Vector3f color) {
        this.getEntityData().set(DATA_COLOR, color);
    }

    public Vector3f getColor() {
        return this.getEntityData().get(DATA_COLOR);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_COLOR, new Vector3f(0.0f, 0.0f, 0.0f));
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (this.isInvulnerableTo(pSource)) {
            return false;
        } else if (pSource.getEntity() instanceof EnderDragon) {
            return false;
        } else {
            if (!this.isRemoved() && !this.level().isClientSide) {
                this.remove(Entity.RemovalReason.KILLED);

                // No explosion

                this.kill();
            }

            return true;
        }
    }
}
