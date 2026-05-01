package com.lovetropics.extras.mixin;

import com.llamalad7.mixinextras.sugar.Local;
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

@Mixin(value = CommonHooks.class, remap = false)
public class ForgeHooksMixin {
    // The Forge event is entirely not useful for our use-case, so let's hook in to the hook
    @Inject(method = "onPlayerTossEvent", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/event/entity/item/ItemTossEvent;<init>(Lnet/minecraft/world/entity/item/ItemEntity;Lnet/minecraft/world/entity/player/Player;)V"), cancellable = true, remap = false)
    private static void onPlayerToss(Player player, ItemStack item, boolean dropAround, boolean includeName, CallbackInfoReturnable<ItemEntity> cir, @Local(name = "ret") ItemEntity ret) {
        // Only if the item actually originated from this player
        if (!includeName) {
            return;
        }
        if (ItemExtensions.onItemToss(player, ret) || CollectibleItemBehavior.onItemToss(player, ret)) {
            cir.setReturnValue(null);
        }
    }
}
