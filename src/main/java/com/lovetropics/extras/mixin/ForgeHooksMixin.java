package com.lovetropics.extras.mixin;

import com.lovetropics.extras.collectible.CollectibleItemSanitizer;
import com.lovetropics.extras.item.UndroppableItems;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = ForgeHooks.class, remap = false)
public class ForgeHooksMixin {
    // The Forge event is entirely not useful for our use-case, so let's hook in to the hook
    @Inject(method = "onPlayerTossEvent", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/event/entity/item/ItemTossEvent;<init>(Lnet/minecraft/world/entity/item/ItemEntity;Lnet/minecraft/world/entity/player/Player;)V"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD, remap = false)
    private static void onPlayerToss(final Player player, final ItemStack item, final boolean includeName, final CallbackInfoReturnable<ItemEntity> cir, final ItemEntity entity) {
        // Only if the item actually originated from this player
        if (!includeName) {
            return;
        }
        if (UndroppableItems.onItemToss(player, entity) || CollectibleItemSanitizer.onItemToss(player, entity)) {
            cir.setReturnValue(null);
        }
    }
}
