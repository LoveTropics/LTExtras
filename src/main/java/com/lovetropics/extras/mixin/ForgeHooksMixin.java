package com.lovetropics.extras.mixin;

import com.lovetropics.extras.collectible.CollectibleItemBehavior;
import com.lovetropics.extras.item.ItemExtensions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.CommonHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = CommonHooks.class, remap = false)
public class ForgeHooksMixin {
    // The Forge event is entirely not useful for our use-case, so let's hook in to the hook
    @Inject(method = "onPlayerTossEvent", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/event/entity/item/ItemTossEvent;<init>(Lnet/minecraft/world/entity/item/ItemEntity;Lnet/minecraft/world/entity/player/Player;)V"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD, remap = false)
    private static void onPlayerToss(Player player, ItemStack item, boolean includeName, CallbackInfoReturnable<ItemEntity> cir, ItemEntity entity) {
        // Only if the item actually originated from this player
        if (!includeName) {
            return;
        }
        if (ItemExtensions.onItemToss(player, entity) || CollectibleItemBehavior.onItemToss(player, entity)) {
            cir.setReturnValue(null);
        }
    }
}
