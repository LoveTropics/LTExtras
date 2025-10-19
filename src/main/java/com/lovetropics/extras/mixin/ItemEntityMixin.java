package com.lovetropics.extras.mixin;

import com.lovetropics.extras.ExtraDataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
    @Shadow
    public abstract ItemStack getItem();

    public ItemEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "getDefaultGravity", at = @At("HEAD"), cancellable = true)
    protected void getDefaultGravity(CallbackInfoReturnable<Double> cir) {
        float gravity = getItem().getOrDefault(ExtraDataComponents.GRAVITY, 0.04f);
        cir.setReturnValue((double) gravity);
    }
}
