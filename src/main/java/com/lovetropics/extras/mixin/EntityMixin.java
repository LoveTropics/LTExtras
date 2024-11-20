package com.lovetropics.extras.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.RelativeMovement;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(Entity.class)
public class EntityMixin {

    @Shadow @Final private Set<String> tags;

    @Unique
    private static final String lTExtras$UNTOUCHABLE = "Untouchable";

    @Inject(method = "kill", at = @At("HEAD"), cancellable = true)
    public void kill(CallbackInfo ci) {
        if(this.tags.contains(lTExtras$UNTOUCHABLE)) {
            ci.cancel();
        }
    }

    @Inject(method = "teleportTo(DDD)V", at = @At("HEAD"), cancellable = true)
    public void teleportToXYZ(CallbackInfo ci) {
        if(this.tags.contains(lTExtras$UNTOUCHABLE)) {
            ci.cancel();
        }
    }
    @Inject(method = "teleportTo(Lnet/minecraft/server/level/ServerLevel;DDDLjava/util/Set;FF)Z", at = @At("HEAD"), cancellable = true)
    public void teleportTo(ServerLevel level, double x, double y, double z, Set<RelativeMovement> relativeMovements, float yRot, float xRot, CallbackInfoReturnable<Boolean> cir) {
        if(this.tags.contains(lTExtras$UNTOUCHABLE)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "moveTo(DDDFF)V", at = @At("HEAD"), cancellable = true)
    public void moveTo(double x, double y, double z, float yRot, float xRot, CallbackInfo ci) {
        if(this.tags.contains(lTExtras$UNTOUCHABLE)) {
            ci.cancel();
        }
    }

    @Inject(method = "setRemoved", at = @At("HEAD"), cancellable = true)
    public final void setRemoved(Entity.RemovalReason removalReason, CallbackInfo ci) {
        if(removalReason == Entity.RemovalReason.KILLED && this.tags.contains(lTExtras$UNTOUCHABLE)) {
            ci.cancel();
        }
    }

}
