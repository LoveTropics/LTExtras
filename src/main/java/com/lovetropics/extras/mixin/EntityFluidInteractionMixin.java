package com.lovetropics.extras.mixin;

import com.lovetropics.extras.effect.ExtraEffects;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityFluidInteraction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(EntityFluidInteraction.class)
public abstract class EntityFluidInteractionMixin {
    @Shadow
    @Final
    private Map<TagKey<Fluid>, EntityFluidInteraction.Tracker> trackerByFluid;

    @Inject(method = "update", at = @At("RETURN"))
    private void update(Entity entity, boolean ignoreCurrent, CallbackInfo ci) {
        if (entity instanceof LivingEntity living && living.hasEffect(ExtraEffects.FISH_EYE)) {
            trackerByFluid.get(FluidTags.WATER).reset();
        }
    }
}
