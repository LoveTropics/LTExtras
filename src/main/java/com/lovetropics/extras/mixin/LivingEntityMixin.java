package com.lovetropics.extras.mixin;

import com.lovetropics.extras.ExtraBlocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    private LivingEntityMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @ModifyConstant(method = "handleRelativeFrictionAndCalculateMovement", constant = @Constant(doubleValue = 0.2))
    private double modifyClimbSpeed(double speed) {
        if (getFeetBlockState().is(ExtraBlocks.PARACORD.get())) {
            return speed * 2.0;
        }
        return speed;
    }
}
