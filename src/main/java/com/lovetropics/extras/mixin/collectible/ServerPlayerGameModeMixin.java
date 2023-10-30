package com.lovetropics.extras.mixin.collectible;

import com.lovetropics.extras.collectible.CollectibleItemBehavior;
import com.lovetropics.extras.item.ItemExtensions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerGameModeMixin {
    @Shadow
    @Final
    protected ServerPlayer player;

    @Redirect(method = "useItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;useOn(Lnet/minecraft/world/item/context/UseOnContext;)Lnet/minecraft/world/InteractionResult;", ordinal = 1))
    private InteractionResult useItemOn(final ItemStack stack, final UseOnContext context) {
        final InteractionResult result = CollectibleItemBehavior.wrapUseOn(stack, context);
        if (result.consumesAction()) {
            ItemExtensions.onItemUsedOn(player, stack, context);
        }
        return result;
    }
}
