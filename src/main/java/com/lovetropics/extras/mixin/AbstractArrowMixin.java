package com.lovetropics.extras.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.lovetropics.extras.ExtraDataComponents;
import com.lovetropics.extras.data.attachment.ExtraAttachments;
import com.lovetropics.extras.item.loot.ArrowItemProjectileOverrideData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Adds support for ARROW_PROJECTILE_OVERRIDE data component to AbstractArrow via #getDefaultGravity() and #onHitEntity()
 * I truly have no clue if this works.
 */

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin {


    @Shadow
    private ItemStack pickupItemStack;

    @Shadow
    protected abstract void readAdditionalSaveData(ValueInput input);

    @Inject(method = "getDefaultGravity", at = @At("HEAD"), cancellable = true)
    protected void getDefaultGravity(CallbackInfoReturnable<Double> returnable) {
        IAttachmentHolder attachments = (IAttachmentHolder) (Object) this;
        if (attachments.hasData(ExtraAttachments.ARROW_GRAVITY_OVERRIDE)) {
            returnable.setReturnValue(attachments.getData(ExtraAttachments.ARROW_GRAVITY_OVERRIDE));
        }
    }



    @WrapOperation(method = "onHitEntity", at = @At(value = "INVOKE", target="Lnet/minecraft/world/entity/Entity;hurtOrSimulate(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    protected boolean onHitEntity(Entity instance, DamageSource source, float damage, Operation<Boolean> original, @Local(name = "damageSource") DamageSource damageSource, @Local() LocalFloatRef damageRef) {
        float finalDamage = damage;

        if (this.pickupItemStack.get(ExtraDataComponents.ARROW_PROJECTILE_OVERRIDE)
                instanceof ArrowItemProjectileOverrideData overrideData
                && overrideData.fixedDamage().isPresent()) {
            finalDamage = Mth.ceil(Mth.clamp(overrideData.fixedDamage().get(), 0.0F, Integer.MAX_VALUE));
        }

        damageRef.set(Mth.ceil(Mth.clamp(finalDamage, 0.0F, Integer.MAX_VALUE)));

        return original.call(instance, source, damageRef.get());
    }

}
