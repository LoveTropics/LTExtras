package com.lovetropics.extras.mixin;

import com.lovetropics.extras.ExtraItems;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemFrame.class)
public abstract class ItemFrameMixin extends Entity {
    private ItemFrameMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Inject(method = "setItem(Lnet/minecraft/world/item/ItemStack;Z)V", at = @At("HEAD"))
    private void setItem(ItemStack stack, boolean pUpdateNeighbours, CallbackInfo ci) {
        if (stack.is(ExtraItems.IMAGE.get())) {
            setInvisible(true);
        }
    }
}
