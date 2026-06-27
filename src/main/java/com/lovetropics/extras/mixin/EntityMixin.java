package com.lovetropics.extras.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.lovetropics.extras.ExtraDataComponents;
import com.lovetropics.extras.ExtraTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.TeleportTransition;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(Entity.class)
public class EntityMixin {

    @Shadow
    @Final
    private Set<String> tags;

//    @Shadow
//    private FluidType forgeFluidTypeOnEyes;
    @Unique
    private static final String lTExtras$UNTOUCHABLE = "Untouchable";

    @Inject(method = "kill", at = @At("HEAD"), cancellable = true)
    public void kill(CallbackInfo ci) {
        if (this.tags.contains(lTExtras$UNTOUCHABLE)) {
            ci.cancel();
        }
    }

    @Inject(method = "teleportTo(DDD)V", at = @At("HEAD"), cancellable = true)
    public void teleportToXYZ(CallbackInfo ci) {
        if (this.tags.contains(lTExtras$UNTOUCHABLE)) {
            ci.cancel();
        }
    }

    @Inject(method = "teleport", at = @At("HEAD"), cancellable = true)
    public void teleportTo(TeleportTransition teleportTransition, CallbackInfoReturnable<Entity> cir) {
        if (this.tags.contains(lTExtras$UNTOUCHABLE)) {
            cir.setReturnValue(null);
        }
    }

    @Inject(method = "snapTo(DDDFF)V", at = @At("HEAD"), cancellable = true)
    public void snapTo(double x, double y, double z, float yRot, float xRot, CallbackInfo ci) {
        if (this.tags.contains(lTExtras$UNTOUCHABLE)) {
            ci.cancel();
        }
    }

    @Inject(method = "setRemoved", at = @At("HEAD"), cancellable = true)
    public final void setRemoved(Entity.RemovalReason removalReason, CallbackInfo ci) {
        if (removalReason == Entity.RemovalReason.KILLED && this.tags.contains(lTExtras$UNTOUCHABLE)) {
            ci.cancel();
        }
    }

    @Inject(method = "nextStep", at = @At("RETURN"), cancellable = true)
    private void nextStep(CallbackInfoReturnable<Float> cir) {
        if ((Object) this instanceof LivingEntity livingEntity) {
            var sound = livingEntity.getItemBySlot(EquipmentSlot.FEET).get(ExtraDataComponents.WALK_SOUND);
            if(sound != null) {
                cir.setReturnValue(livingEntity.moveDist + sound.cooldown().sample(livingEntity.getRandom()));
            }
        }
    }

    @Inject(method = "walkingStepSound", at = @At("HEAD"), cancellable = true)
    private void walkingStepSound(BlockPos pos, BlockState state, CallbackInfo ci) {
        if ((Object) this instanceof LivingEntity livingEntity) {
            var sound = livingEntity.getItemBySlot(EquipmentSlot.FEET).get(ExtraDataComponents.WALK_SOUND);
            if(sound != null) {
                livingEntity.playSound(sound.soundEvent().value(), sound.volume().sample(livingEntity.getRandom()), sound.pitch().sample(livingEntity.getRandom()));
                if(!sound.playOtherSounds()) {
                    ci.cancel();
                }
            }
        }
    }

    // Allows for /ride to work with players
    @ModifyExpressionValue(method = "startRiding(Lnet/minecraft/world/entity/Entity;ZZ)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityType;canSerialize()Z"))
    private boolean skipRiddingChecks(boolean original) {
        return true;
    }

    @Inject(method = "isPushable", at= @At("HEAD"), cancellable = true)
    private void isPushable(CallbackInfoReturnable<Boolean> cir) {
        if(this.tags.contains(ExtraTags.NO_PUSH)) {
            cir.setReturnValue(false);
        }
    }
}
