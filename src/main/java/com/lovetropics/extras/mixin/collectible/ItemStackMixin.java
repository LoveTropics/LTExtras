package com.lovetropics.extras.mixin.collectible;

import com.lovetropics.extras.collectible.CollectibleItemBehavior;
import com.lovetropics.extras.item.ItemExtensions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Redirect(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResultHolder;"))
    private InteractionResultHolder<ItemStack> useItem(Item item, Level level, Player player, InteractionHand hand) {
        InteractionResultHolder<ItemStack> result = CollectibleItemBehavior.wrapUse((ItemStack) (Object) this, level, player, hand);
        if (player instanceof ServerPlayer serverPlayer && result.getResult().consumesAction()) {
            ItemExtensions.onItemUsed(serverPlayer, result.getObject());
        }
        return result;
    }
}
