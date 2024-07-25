package com.lovetropics.extras.mixin.collectible;

import com.lovetropics.extras.collectible.Collectible;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Slot.class)
public class SlotMixin {
    @Shadow
    @Final
    public Container container;

    @Inject(method = "setByPlayer(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)V", at = @At("HEAD"), cancellable = true)
    private void setByPlayer(ItemStack stack, ItemStack oldStack, CallbackInfo ci) {
        if (!(container instanceof Inventory) && Collectible.isCollectible(stack)) {
            ci.cancel();
        }
    }
}
