package com.lovetropics.extras.entity;

import com.lovetropics.extras.ExtraItems;
import com.lovetropics.extras.ExtraLangKeys;
import com.lovetropics.extras.effect.ExtraEffects;
import com.lovetropics.extras.model_modifer.ModelModifierType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * LTExtras
 * FIRE EMOJI, FIRE EMOJI, FIRE EMOJI
 */
public class WaterCoolerEntity extends Entity {
    private static final EntityDataAccessor<Integer> DATA_SHAKE_TIME = SynchedEntityData.defineId(WaterCoolerEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SHAKE_TYPE = SynchedEntityData.defineId(WaterCoolerEntity.class, EntityDataSerializers.INT);

    private static final RandomSource random = RandomSource.create();
    private static final ModelModifierType[] MODIFIER_TYPES = {
            ModelModifierType.FABULOUS,
            ModelModifierType.FLAIL,
            ModelModifierType.HOVERING,
            ModelModifierType.SHUFFLE,
            ModelModifierType.UPSIDEDOWN,
            ModelModifierType.SHRUNK,
            ModelModifierType.ENLARGED,
            ModelModifierType.SHRUGGY_ARMS,
            ModelModifierType.ENDER_ARMS,
    };

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
        builder.define(SHAKE_TYPE, 0);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            this.setupAnimationStates();
        }

        if (this.isShaking()) {
            entityData.set(DATA_SHAKE_TIME, this.getShakeTime() - 1);
        }
    }

    @Override
    public boolean hurtServer(ServerLevel serverLevel, DamageSource damageSource, float v) {
        return false;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand, Vec3 location) {
        if (level().isClientSide()) return InteractionResult.SUCCESS;
        if (!this.isShaking()) {
            if (random.nextInt(100) >= 90) {
                dispense(level());
            } else {
                shake();
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    public void shake() {
        int random = getRandom().nextInt(3);
        entityData.set(SHAKE_TYPE, random);
        entityData.set(DATA_SHAKE_TIME, 5);
    }

    public void dispense(Level level) {
        entityData.set(SHAKE_TYPE, 3);
        entityData.set(DATA_SHAKE_TIME, 6);
        final ItemEntity itemEntity = new ItemEntity(level, getX(), getY()+1.25f, getZ(), getPotionDrop());
        level.addFreshEntity(itemEntity);
    }

    private void setupAnimationStates() {
        int shakeType = this.entityData.get(SHAKE_TYPE);
        this.shake1AnimationState.animateWhen(shakeType == 0 && this.isShaking(), this.tickCount);
        this.shake2AnimationState.animateWhen(shakeType == 1 && this.isShaking(), this.tickCount);
        this.shake3AnimationState.animateWhen(shakeType == 2 && this.isShaking(), this.tickCount);
        this.shakeDispenseAnimationState.animateWhen(shakeType == 3 && this.isShaking(), this.tickCount);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        entityData.set(DATA_SHAKE_TIME, input.getIntOr("shake_time", 0));
        entityData.set(SHAKE_TYPE, input.getIntOr("shake_type", 0));
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        output.putInt("shake_time", this.getShakeTime());
        output.putInt("shake_type", this.entityData.get(SHAKE_TYPE));
    }

    @Override
    public boolean canBeCollidedWith(@Nullable Entity entity) {
        return false;
    }

    public int getShakeTime() {
        return this.getEntityData().get(DATA_SHAKE_TIME);
    }

    public boolean isShaking() {
        return getShakeTime() > 0;
    }

    private static ItemStack getPotionDrop() {
        MobEffectInstance mobEffect = new MobEffectInstance(ExtraEffects.MODEL_EFFECTS.get(Util.getRandom(MODIFIER_TYPES, random)), 15 * 20, 1);
        PotionContents potionContents = new PotionContents(Optional.empty(), Optional.of(0x5A8DD6), List.of(), Optional.of(ExtraLangKeys.WATER_COOLER_POTION_NAME.get().toString())).withEffectAdded(mobEffect);
        ItemStack itemStack = new ItemStack(Items.POTION, 1);
        itemStack.set(DataComponents.POTION_CONTENTS, potionContents);
        itemStack.set(DataComponents.CUSTOM_NAME, ExtraLangKeys.WATER_COOLER_POTION_NAME.get());
        itemStack.set(DataComponents.LORE, ItemLore.EMPTY.withLineAdded(ExtraLangKeys.WATER_COOLER_POTION_LORE.get().withColor(TextColor.GRAY)));
        itemStack.set(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT
                .withHidden(DataComponents.ATTRIBUTE_MODIFIERS, true)
                .withHidden(DataComponents.POTION_CONTENTS, true)
        );
        return itemStack;
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
