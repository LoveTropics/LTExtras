package com.lovetropics.extras.mixin.collectible;

import com.lovetropics.extras.collectible.Collectible;
import com.lovetropics.extras.collectible.CollectibleStore;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantResultSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantResultSlot.class)
public class MerchantResultSlotMixin {
    @Shadow
    @Final
    private Player player;

    @Inject(method = "checkTakeAchievements", at = @At("HEAD"))
    private void onTake(ItemStack stack, CallbackInfo ci) {
        Holder<Collectible> collectible = Collectible.byItem(stack);
        if (collectible == null) {
            return;
        }
        CollectibleStore store = CollectibleStore.get(player);
        store.give(collectible);
        Collectible.addMarkerTo(player.getUUID(), collectible, stack);
    }
}
