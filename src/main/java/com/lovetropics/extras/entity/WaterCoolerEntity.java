package com.lovetropics.extras.entity;

import com.lovetropics.extras.ExtraItems;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HoneyBlock;
import net.minecraft.world.level.block.TintedGlassBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * LTExtras
 * FIRE EMOJI, FIRE EMOJI, FIRE EMOJI
 */
public class WaterCoolerEntity extends Entity {
    private static final EntityDataAccessor<Integer> DATA_SHAKE_TIME = SynchedEntityData.defineId(WaterCoolerEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_DISPENSE_TIME = SynchedEntityData.defineId(WaterCoolerEntity.class, EntityDataSerializers.INT);

    private int shakeAnimationRemainingTicks;
    private int dispenseAnimationRemainingTicks;

    public final AnimationState shake1AnimationState = new AnimationState();
    public final AnimationState shake2AnimationState = new AnimationState();
    public final AnimationState shake3AnimationState = new AnimationState();
    public final AnimationState shakeDispenseAnimationState = new AnimationState();

    public WaterCoolerEntity(EntityType<? extends Entity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_SHAKE_TIME, 0);
        builder.define(DATA_DISPENSE_TIME, 0);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.shakeAnimationRemainingTicks > 0) {
            shake1AnimationState.startIfStopped(tickCount);
            this.shakeAnimationRemainingTicks--;
            entityData.set(DATA_SHAKE_TIME, this.shakeAnimationRemainingTicks);
        }

        if (this.dispenseAnimationRemainingTicks > 0) {
            shakeDispenseAnimationState.startIfStopped(tickCount);
            this.dispenseAnimationRemainingTicks--;
            entityData.set(DATA_DISPENSE_TIME, this.dispenseAnimationRemainingTicks);
        } else {
            shakeDispenseAnimationState.stop();
        }

        if (this.level().isClientSide) {
            this.setupAnimationStates();
        }
    }

    @Override
    public boolean hurtServer(ServerLevel serverLevel, DamageSource damageSource, float v) {
        return false;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (level().isClientSide) return InteractionResult.SUCCESS;
        if (!this.isShaking() && !this.isDispensing()) {
            this.tryShake();
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    public void tryShake() {
        this.shakeAnimationRemainingTicks = 50;
        entityData.set(DATA_SHAKE_TIME, 50);
    }

    public void tryDispense() {
        this.dispenseAnimationRemainingTicks = 6;
        entityData.set(DATA_DISPENSE_TIME, 6);
    }

    private void setupAnimationStates() {
        this.shake1AnimationState.animateWhen(this.shakeAnimationRemainingTicks > 0, this.shakeAnimationRemainingTicks);
        //this.shake2AnimationState
        //this.shake3AnimationState
        this.shakeDispenseAnimationState.animateWhen(this.dispenseAnimationRemainingTicks > 0, this.tickCount);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        entityData.set(DATA_SHAKE_TIME, input.getIntOr("shake_time", 0));
        entityData.set(DATA_DISPENSE_TIME, input.getIntOr("dispense_time", 0));
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        output.putInt("shake_time", entityData.get(DATA_SHAKE_TIME));
        output.putInt("dispense_time", entityData.get(DATA_DISPENSE_TIME));
    }

    @Override
    public boolean canBeCollidedWith(@Nullable Entity entity) {
        return true;
    }

    public boolean isShaking() {
        return this.getEntityData().get(DATA_SHAKE_TIME) > 0;
    }

    public int getShakeTime() {
        return this.getEntityData().get(DATA_SHAKE_TIME);
    }

    public boolean isDispensing() {
        return this.getEntityData().get(DATA_DISPENSE_TIME) > 0;
    }

    public int getDispenseTime() {
        return this.getEntityData().get(DATA_DISPENSE_TIME);
    }

    @Override
    protected AABB makeBoundingBox(Vec3 position) {
        return AABB.ofSize(position.add(new Vec3(0.0f, 0.65f, 0.0f)), 0.75f, 1.75f, 0.75f);
    }

    @Override
    public final ItemStack getPickResult() {
        return new ItemStack(ExtraItems.WATER_COOLER_SPAWN_EGG.get());
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return false;
    }
}
