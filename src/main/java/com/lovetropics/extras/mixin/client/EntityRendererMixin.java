package com.lovetropics.extras.mixin.client;

import com.lovetropics.extras.ExtraItems;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity> {
    @Inject(method = "affectedByCulling", at = @At("HEAD"), cancellable = true)
    private void affectedByCulling(T entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof LivingEntity living) {
            ItemStack headItem = living.getItemBySlot(EquipmentSlot.HEAD);
            if (headItem.is(ExtraItems.STACK_OF_HATS)) {
                cir.setReturnValue(false);
            }
        }
    }
}
