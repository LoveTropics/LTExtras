package com.lovetropics.extras.collectible;

import com.lovetropics.extras.LTExtras;
import net.minecraft.SharedConstants;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerDestroyItemEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = LTExtras.MODID)
public class CollectibleItemBehavior {
    private static final int INVENTORY_CHECK_INTERVAL = SharedConstants.TICKS_PER_SECOND;

    public static boolean onItemToss(Player player, ItemEntity item) {
        ItemStack stack = item.getItem();
        if (Collectible.isCollectible(stack)) {
            player.addItem(stack);
            return true;
        }
        return false;
    }

    @SubscribeEvent
    public static void onItemPickup(ItemEntityPickupEvent.Pre event) {
        ItemStack stack = event.getItemEntity().getItem();
        Collectible collectible = Collectible.byItem(stack);
        if (collectible == null) {
            return;
        }

        Player player = event.getPlayer();
        if (Collectible.isIllegalCollectible(stack, player)) {
            stack.setCount(0);
            event.getItemEntity().discard();
            event.setCanPickup(TriState.FALSE);
        } else {
            CollectibleStore store = CollectibleStore.get(player);
            store.give(collectible);
            Collectible.addMarkerTo(player.getUUID(), stack);
        }
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        event.getDrops().removeIf(item -> Collectible.isCollectible(item.getItem()));
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer && serverPlayer.tickCount % INVENTORY_CHECK_INTERVAL == 0) {
            Inventory inventory = serverPlayer.getInventory();
            int count = inventory.clearOrCountMatchingItems(stack -> Collectible.isIllegalCollectible(stack, serverPlayer), -1, serverPlayer.inventoryMenu.getCraftSlots());
            if (count > 0) {
                serverPlayer.containerMenu.broadcastChanges();
                serverPlayer.inventoryMenu.slotsChanged(inventory);
            }
        }
    }

    @SubscribeEvent
    public static void onDestroyItem(PlayerDestroyItemEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ItemStack item = event.getOriginal();
            if (Collectible.isCollectible(item)) {
                player.addItem(item);
            }
        }
    }

    @SubscribeEvent
    public static void onFinishUsingItem(LivingEntityUseItemEvent.Finish event) {
        ItemStack stack = event.getItem();
        if (Collectible.isCollectible(stack) && event.getResultStack().isEmpty()) {
            event.setResultStack(stack);
        }
    }

    public static InteractionResultHolder<ItemStack> wrapUse(ItemStack stack, Level level, Player player, InteractionHand hand) {
        if (Collectible.isCollectible(stack)) {
            int count = stack.getCount();
            InteractionResultHolder<ItemStack> result = stack.getItem().use(level, player, hand);
            stack.setCount(count);
            return result;
        }
        return stack.getItem().use(level, player, hand);
    }

    public static InteractionResult wrapUseOn(ItemStack stack, UseOnContext context) {
        if (Collectible.isCollectible(stack)) {
            int count = stack.getCount();
            InteractionResult result = stack.useOn(context);
            stack.setCount(count);
            return result;
        }
        return stack.useOn(context);
    }
}
