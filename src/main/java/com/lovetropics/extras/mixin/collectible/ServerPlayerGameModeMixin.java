package com.lovetropics.extras.mixin.collectible;

import com.lovetropics.extras.collectible.CollectibleItemBehavior;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerGameModeMixin {
    @Redirect(method = "useItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResultHolder;"))
    private InteractionResultHolder<ItemStack> useItem(final ItemStack stack, final Level level, final Player player, final InteractionHand hand) {
        return CollectibleItemBehavior.wrapUse(stack, level, player, hand);
    }

    @Redirect(method = "useItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;useOn(Lnet/minecraft/world/item/context/UseOnContext;)Lnet/minecraft/world/InteractionResult;", ordinal = 1))
    private InteractionResult useItemOn(final ItemStack stack, final UseOnContext context) {
        return CollectibleItemBehavior.wrapUseOn(stack, context);
    }
}
