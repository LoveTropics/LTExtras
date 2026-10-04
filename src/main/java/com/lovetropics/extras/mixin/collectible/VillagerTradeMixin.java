package com.lovetropics.extras.mixin.collectible;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.lovetropics.extras.ExtraDataComponents;
import com.lovetropics.extras.collectible.Collectible;
import com.lovetropics.extras.collectible.CollectibleMarker;
import net.minecraft.util.Util;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.trading.VillagerTrade;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(VillagerTrade.class)
public class VillagerTradeMixin {
    // Make it a bit easier to define an item by a collectible id, just recreate the itemstack
    @WrapOperation(method = "getOffer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStackTemplate;create()Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack createOutput(ItemStackTemplate instance, Operation<ItemStack> original) {
        ItemStack itemStack = original.call(instance);
        CollectibleMarker marker = itemStack.get(ExtraDataComponents.COLLECTIBLE);
        if (marker != null && marker.collectible().isPresent()) {
            return Collectible.createItemStack(marker.collectible().get(), Util.NIL_UUID);
        }
        return itemStack;
    }
}
