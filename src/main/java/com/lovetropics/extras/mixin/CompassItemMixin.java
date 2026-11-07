package com.lovetropics.extras.mixin;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CompassItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CompassItem.class)
public class CompassItemMixin {

    @Inject(method = "getName", at=@At("HEAD"), cancellable = true)
    public void getName(ItemStack itemStack, CallbackInfoReturnable<Component> cir) {
        if(itemStack.has(DataComponents.ITEM_NAME)){
            cir.setReturnValue(itemStack.get(DataComponents.ITEM_NAME));
        }
    }
}
