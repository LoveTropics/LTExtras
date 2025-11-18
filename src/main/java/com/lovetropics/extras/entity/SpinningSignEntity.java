package com.lovetropics.extras.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class SpinningSignEntity extends Entity {

    private static final EntityDataAccessor<Float> DATA_SCALE = SynchedEntityData.defineId(SpinningSignEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Optional<Component>> DATA_TEXT = SynchedEntityData.defineId(SpinningSignEntity.class, EntityDataSerializers.OPTIONAL_COMPONENT);
    private static final EntityDataAccessor<ItemStack> DATA_ITEM = SynchedEntityData.defineId(SpinningSignEntity.class, EntityDataSerializers.ITEM_STACK);

    public SpinningSignEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        noPhysics = true;
    }


    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_SCALE, 1F);
        builder.define(DATA_TEXT, Optional.empty());
        builder.define(DATA_ITEM, ItemStack.EMPTY);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (DATA_SCALE.equals(key)) {
            refreshDimensions();
        }
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource damageSource, float amount) {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        entityData.set(DATA_SCALE, input.getFloatOr("Scale", 1F));
        entityData.set(DATA_TEXT, input.read("Text", ComponentSerialization.CODEC));
        entityData.set(DATA_ITEM, input.read("Item", ItemStack.CODEC).orElse(ItemStack.EMPTY));
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        output.putFloat("Scale", entityData.get(DATA_SCALE));
        output.storeNullable("Text", ComponentSerialization.CODEC, entityData.get(DATA_TEXT).orElse(null));
        output.storeNullable("Item", ItemStack.CODEC, entityData.get(DATA_ITEM));
    }

    @Override
    public boolean canBeCollidedWith(@Nullable Entity entity) {
        return false;
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return false;
    }

    public float getScale(){
        return this.getEntityData().get(DATA_SCALE);
    }

    public Optional<Component> getText(){
        return this.entityData.get(DATA_TEXT);
    }

    public ItemStack getItem(){
        return this.entityData.get(DATA_ITEM);
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    protected AABB makeBoundingBox(Vec3 position) {
        float sizeXZ = getScale() * 3.35f;
        float sizeY = getScale() * 0.75f;
        return AABB.ofSize(position, sizeXZ, sizeY, sizeXZ);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        // This is bad but we will accept it for now :)
        return true;
    }
}
