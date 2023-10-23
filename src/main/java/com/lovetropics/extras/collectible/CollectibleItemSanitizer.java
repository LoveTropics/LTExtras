package com.lovetropics.extras.collectible;

import com.lovetropics.extras.LTExtras;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = LTExtras.MODID)
public class CollectibleItemSanitizer {
    @SubscribeEvent
    public static void onItemToss(final ItemTossEvent event) {
        final ItemStack stack = event.getEntity().getItem();
        if (Collectible.isCollectible(stack)) {
            event.getPlayer().addItem(stack);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onItemPickup(final EntityItemPickupEvent event) {
        final ItemStack stack = event.getItem().getItem();
        if (Collectible.isCollectible(stack)) {
            stack.setCount(0);
            event.getItem().discard();
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLivingDrops(final LivingDropsEvent event) {
        event.getDrops().removeIf(item -> Collectible.isCollectible(item.getItem()));
    }
}
