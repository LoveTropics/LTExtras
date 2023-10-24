package com.lovetropics.extras.item;

import com.lovetropics.extras.LTExtras;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = LTExtras.MODID)
public class UndroppableItems {
    private static final String TAG_UNDROPPABLE = "undroppable";

    @SubscribeEvent
    public static void onItemToss(final ItemTossEvent event) {
        final ItemStack stack = event.getEntity().getItem();
        if (isUndroppable(stack)) {
            event.setCanceled(true);
            event.getPlayer().addItem(stack);
        }
    }

    @SubscribeEvent
    public static void onItemPickup(final EntityItemPickupEvent event) {
        final ItemStack stack = event.getItem().getItem();
        if (isUndroppable(stack)) {
            stack.setCount(0);
            event.getItem().discard();
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLivingDrops(final LivingDropsEvent event) {
        event.getDrops().removeIf(item -> isUndroppable(item.getItem()));
    }

    private static boolean isUndroppable(final ItemStack stack) {
        return stack.getTag() != null && stack.getTag().contains(TAG_UNDROPPABLE);
    }
}
