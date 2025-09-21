package com.lovetropics.extras.mixin.collectible;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.lovetropics.extras.collectible.CollectibleItemBehavior;
import com.lovetropics.extras.item.ItemExtensions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @WrapOperation(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;"))
    private InteractionResult useItem(Item item, Level level, Player player, InteractionHand hand, Operation<InteractionResult> original) {
        ItemStack stack = (ItemStack) (Object) this;
        InteractionResult result = CollectibleItemBehavior.wrapUse(stack, level, player, hand, original);
        if (player instanceof ServerPlayer serverPlayer && result.consumesAction()) {
            ItemStack resultStack = player.getItemInHand(hand);
            if (result instanceof InteractionResult.Success success && success.heldItemTransformedTo() != null) {
                resultStack = success.heldItemTransformedTo();
            }
            ItemExtensions.onItemUsed(serverPlayer, resultStack);
        }
        return result;
    }
}
