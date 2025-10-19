package com.lovetropics.extras.mixin;

import com.lovetropics.extras.effect.ExtraEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.portal.TeleportTransition;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidType;
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

    @Shadow
    private FluidType forgeFluidTypeOnEyes;
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

    @Inject(method = "updateFluidOnEyes", at = @At("RETURN"))
    private void updateFluidOnEyes(CallbackInfo ci) {
        if ((Object) this instanceof LivingEntity livingEntity && livingEntity.hasEffect(ExtraEffects.FISH_EYE)) {
            forgeFluidTypeOnEyes = NeoForgeMod.EMPTY_TYPE.value();
        }
    }
}
