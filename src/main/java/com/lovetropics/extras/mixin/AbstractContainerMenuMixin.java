package com.lovetropics.extras.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.lovetropics.extras.ExtraDataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.RemoteSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerMenu.class)
public class AbstractContainerMenuMixin {
    @Inject(
            method = "moveItemStackTo(Lnet/minecraft/world/item/ItemStack;IIZ)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isStackable()Z"),
            cancellable = true)
    private void quickMoveStack(ItemStack itemStack, int startSlot, int endSlot, boolean backwards, CallbackInfoReturnable<Boolean> cir) {
        if(itemStack.has(ExtraDataComponents.UNDROPPABLE)){
            if(!((startSlot == 9 && endSlot == 36) || (startSlot == 36 && endSlot == 45))){
                cir.setReturnValue(false);
            }
        }
    }
}
