package com.lovetropics.extras.mixin.collectible;

import com.lovetropics.extras.collectible.Collectible;
import com.lovetropics.extras.collectible.CollectibleStore;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantResultSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantResultSlot.class)
public class MerchantResultSlotMixin {
    @Inject(method = "onTake", at = @At("HEAD"))
    private void onTake(final Player player, final ItemStack stack, final CallbackInfo ci) {
        final Collectible collectible = Collectible.byItem(stack);
        if (collectible == null) {
            return;
        }
        final CollectibleStore store = CollectibleStore.getNullable(player);
        if (store != null) {
            store.give(collectible);
            Collectible.addMarkerTo(player.getUUID(), stack);
        }
    }
}
