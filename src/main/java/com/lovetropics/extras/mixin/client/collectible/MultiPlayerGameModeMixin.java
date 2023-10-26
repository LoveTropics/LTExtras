package com.lovetropics.extras.mixin.client.collectible;

import com.lovetropics.extras.collectible.CollectibleItemBehavior;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {
    @Redirect(method = "performUseItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;useOn(Lnet/minecraft/world/item/context/UseOnContext;)Lnet/minecraft/world/InteractionResult;", ordinal = 1))
    private InteractionResult useItemOn(final ItemStack stack, final UseOnContext context) {
        return CollectibleItemBehavior.wrapUseOn(stack, context);
    }
}
