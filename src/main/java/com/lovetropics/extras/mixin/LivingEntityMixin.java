package com.lovetropics.extras.mixin;

import com.lovetropics.extras.ExtraTags;
import com.lovetropics.extras.effect.ExtraEffects;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


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
    public boolean isPushedByFluid(FluidType type) {
        if (type == NeoForgeMod.WATER_TYPE.value() && hasEffect(ExtraEffects.FISH_EYE)) {
            return false;
        }
        return super.isPushedByFluid(type);
    }

    @Inject(method = "isPushable", at= @At("HEAD"), cancellable = true)
    private void isPushable(CallbackInfoReturnable<Boolean> cir) {
        if(this.entityTags().contains(ExtraTags.NO_PUSH)) {
            cir.setReturnValue(false);
        }
    }
}
