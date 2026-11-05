package com.lovetropics.extras.mixin;

import com.lovetropics.extras.ExtraDataComponents;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerMenu.class)
public class AbstractContainerMenuMixin {
    @Shadow
    @Final
    public NonNullList<Slot> slots;

    @Inject(
            method = "moveItemStackTo(Lnet/minecraft/world/item/ItemStack;IIZ)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isStackable()Z"),
            cancellable = true)
    private void quickMoveStack(ItemStack itemStack, int startSlot, int endSlot, boolean backwards, CallbackInfoReturnable<Boolean> cir) {
        if (itemStack.has(ExtraDataComponents.UNDROPPABLE)) {
            // Moving into the player inventory is fine
            if (slots.get(startSlot).container instanceof Inventory) {
                return;
            }
            cir.setReturnValue(false);
        }
    }
}
