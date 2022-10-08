package com.lovetropics.extras.mixin;

import com.lovetropics.extras.effect.ExtraEffects;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
    private PlayerMixin(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public boolean updateFluidHeightAndDoFluidPushing(TagKey<Fluid> fluid, double scale) {
        if (fluid == FluidTags.WATER && hasEffect(ExtraEffects.FISH_EYE.get())) {
            return false;
        }
        return super.updateFluidHeightAndDoFluidPushing(fluid, scale);
    }

    @Override
    public boolean isEyeInFluid(TagKey<Fluid> fluid) {
        if (fluid == FluidTags.WATER && hasEffect(ExtraEffects.FISH_EYE.get())) {
            return false;
        }
        return super.isEyeInFluid(fluid);
    }
}
