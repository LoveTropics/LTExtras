package com.lovetropics.extras.mixin;

import com.lovetropics.extras.ExtraDataComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CrossbowItem.class)
public class CrossbowItemMixin {

    @Inject(method = "getChargeDuration", at = @At("HEAD"), cancellable = true)
    private static void getChargeDuration(ItemStack crossbow, LivingEntity user, CallbackInfoReturnable<Integer> cir) {
        if (crossbow.has(ExtraDataComponents.CROSSBOW_CHARGE_TIME)) {
            cir.setReturnValue(Mth.floor(crossbow.get(ExtraDataComponents.CROSSBOW_CHARGE_TIME).floatValue() * 20.0F));
        }
    }
}
