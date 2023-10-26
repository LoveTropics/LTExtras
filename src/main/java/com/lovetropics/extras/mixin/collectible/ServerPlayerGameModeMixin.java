package com.lovetropics.extras.mixin.collectible;

import com.lovetropics.extras.collectible.CollectibleItemBehavior;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerGameModeMixin {
    @Redirect(method = "useItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;useOn(Lnet/minecraft/world/item/context/UseOnContext;)Lnet/minecraft/world/InteractionResult;", ordinal = 1))
    private InteractionResult useItemOn(final ItemStack stack, final UseOnContext context) {
        return CollectibleItemBehavior.wrapUseOn(stack, context);
    }
}
