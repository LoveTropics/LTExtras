package com.lovetropics.extras.mixin;

import com.lovetropics.extras.ExtraTags;
import com.lovetropics.extras.effect.ExtraEffects;
import net.minecraft.core.Holder;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	private LivingEntityMixin(EntityType<?> type, Level level) {
		super(type, level);
	}

	@Shadow
	public abstract boolean hasEffect(Holder<MobEffect> effect);

	@ModifyConstant(method = "handleRelativeFrictionAndCalculateMovement", constant = @Constant(doubleValue = 0.2))
	private double modifyClimbSpeed(double speed) {
		BlockState state = getInBlockState();
		if (state.is(ExtraTags.Blocks.CLIMBABLE_VERY_FAST)) {
			return speed * 2.0;
		} else if (state.is(ExtraTags.Blocks.CLIMBABLE_FAST)) {
			return speed * 1.5;
		}
		return speed;
	}

	@Override
	public boolean updateFluidHeightAndDoFluidPushing(TagKey<Fluid> fluid, double scale) {
		if (fluid == FluidTags.WATER && hasEffect(ExtraEffects.FISH_EYE)) {
			return false;
		}
		return super.updateFluidHeightAndDoFluidPushing(fluid, scale);
	}

	@Override
	public boolean isEyeInFluid(TagKey<Fluid> fluid) {
		if (fluid == FluidTags.WATER && hasEffect(ExtraEffects.FISH_EYE)) {
			return false;
		}
		return super.isEyeInFluid(fluid);
	}
}
