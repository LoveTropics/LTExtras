package com.lovetropics.extras.collectible;

import com.lovetropics.extras.LTExtras;
import net.minecraft.SharedConstants;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = LTExtras.MODID)
public class CollectibleItemSanitizer {
    private static final int INVENTORY_CHECK_INTERVAL = SharedConstants.TICKS_PER_SECOND;

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

    @SubscribeEvent
    public static void onPlayerTick(final TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            return;
        }
        final Player player = event.player;
        if (player instanceof final ServerPlayer serverPlayer && serverPlayer.tickCount % INVENTORY_CHECK_INTERVAL == 0) {
            final Inventory inventory = serverPlayer.getInventory();
            final int count = inventory.clearOrCountMatchingItems(stack -> Collectible.isIllegalCollectible(stack, serverPlayer), -1, serverPlayer.inventoryMenu.getCraftSlots());
            if (count > 0) {
                serverPlayer.containerMenu.broadcastChanges();
                serverPlayer.inventoryMenu.slotsChanged(inventory);
            }
        }
    }
}
