package com.lovetropics.extras.mixin;

import com.lovetropics.extras.effect.ExtraEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityFluidInteraction;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Mixin(EntityFluidInteraction.class)
public abstract class EntityFluidInteractionMixin {

    @Shadow
    protected abstract EntityFluidInteraction.Tracker getTrackerFor(FluidType fluid);

    @Inject(method = "update(Lnet/minecraft/world/entity/Entity;Ljava/util/function/Predicate;)V", at = @At("RETURN"))
    private void update(Entity entity, Predicate<FluidType> typePushPredicate, CallbackInfo ci) {
        if (entity instanceof LivingEntity living && living.hasEffect(ExtraEffects.FISH_EYE)) {
            this.getTrackerFor(NeoForgeMod.WATER_TYPE.value()).reset();
        }
    }
}
