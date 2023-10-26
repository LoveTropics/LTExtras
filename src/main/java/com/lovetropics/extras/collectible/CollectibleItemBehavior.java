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
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = LTExtras.MODID)
public class CollectibleItemBehavior {
    private static final int INVENTORY_CHECK_INTERVAL = SharedConstants.TICKS_PER_SECOND;

    public static boolean onItemToss(final Player player, final ItemEntity item) {
        final ItemStack stack = item.getItem();
        if (Collectible.isCollectible(stack)) {
            player.addItem(stack);
            return true;
        }
        return false;
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

    @SubscribeEvent
    public static void onDestroyItem(final PlayerDestroyItemEvent event) {
        if (event.getEntity() instanceof final ServerPlayer player) {
            final ItemStack item = event.getOriginal();
            if (Collectible.isCollectible(item)) {
                player.addItem(item);
            }
        }
    }

    @SubscribeEvent
    public static void onFinishUsingItem(final LivingEntityUseItemEvent.Finish event) {
        final ItemStack stack = event.getItem();
        if (Collectible.isCollectible(stack) && event.getResultStack().isEmpty()) {
            event.setResultStack(stack);
        }
    }

    public static InteractionResultHolder<ItemStack> wrapUse(final ItemStack stack, final Level level, final Player player, final InteractionHand hand) {
        if (Collectible.isCollectible(stack)) {
            final int count = stack.getCount();
            final InteractionResultHolder<ItemStack> result = stack.getItem().use(level, player, hand);
            stack.setCount(count);
            return result;
        }
        return stack.getItem().use(level, player, hand);
    }

    public static InteractionResult wrapUseOn(final ItemStack stack, final UseOnContext context) {
        if (Collectible.isCollectible(stack)) {
            final int count = stack.getCount();
            final InteractionResult result = stack.useOn(context);
            stack.setCount(count);
            return result;
        }
        return stack.useOn(context);
    }
}
