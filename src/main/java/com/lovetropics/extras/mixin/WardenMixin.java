package com.lovetropics.extras.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Warden.class)
public class WardenMixin extends Monster {

    private boolean avoidsDarkness;

    protected WardenMixin(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    @Inject(method = "getWalkTargetValue", at = @At("HEAD"), cancellable = true)
    public void getWalkTargetValue(BlockPos pos, LevelReader level, CallbackInfoReturnable<Float> cir) {
        if (avoidsDarkness) {
            if (level.getRawBrightness(pos, 0) > 0) {
                cir.setReturnValue(-60.0f);
            }
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        avoidsDarkness = input.getBooleanOr("AvoidsDarkness", false);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("AvoidsDarkness", avoidsDarkness);
    }
}
