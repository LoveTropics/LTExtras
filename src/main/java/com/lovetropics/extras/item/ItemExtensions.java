package com.lovetropics.extras.item;

import com.lovetropics.extras.ExtraDataComponents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.TriState;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.portal.TeleportTransition;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

@EventBusSubscriber
public class ItemExtensions {
    public static @Nullable ItemStack dropSelected(Inventory inventory, boolean all) {
        ItemStack selectedItem = inventory.getItem(inventory.getSelectedSlot());
        if (!selectedItem.has(ExtraDataComponents.UNDROPPABLE) && selectedItem.has(ExtraDataComponents.INFINITE)) {
            return all ? selectedItem.copy() : selectedItem.copyWithCount(1);
        }
        return null;
    }

    public static boolean onItemToss(Player player, ItemEntity item) {
        ItemStack stack = item.getItem();
        if (stack.has(ExtraDataComponents.UNDROPPABLE)) {
            player.addItem(stack);
            return true;
        }
        if (stack.has(ExtraDataComponents.INFINITE)) {
            stack.remove(ExtraDataComponents.INFINITE);
        }
        return false;
    }

    @SubscribeEvent
    public static void onItemPickup(ItemEntityPickupEvent.Pre event) {
        ItemStack stack = event.getItemEntity().getItem();
        if (stack.has(ExtraDataComponents.UNDROPPABLE)) {
            stack.setCount(0);
            event.getItemEntity().discard();
            event.setCanPickup(TriState.FALSE);
            return;
        }

        Integer maxPickedUp = stack.get(ExtraDataComponents.MAX_PICKED_UP);
        if (maxPickedUp != null) {
            Player player = event.getPlayer();
            int inventoryCount = player.getInventory().clearOrCountMatchingItems(
                    inventoryStack -> ItemStack.isSameItemSameComponents(stack, inventoryStack),
                    0,
                    player.inventoryMenu.getCraftSlots()
            );
            if (inventoryCount + stack.getCount() > maxPickedUp) {
                event.setCanPickup(TriState.FALSE);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        event.getDrops().removeIf(item -> item.getItem().has(ExtraDataComponents.UNDROPPABLE));
    }

    public static void onItemUsedOn(ServerPlayer player, ItemStack stack, UseOnContext context) {
        if (!player.isUsingItem()) {
            applyCooldownOverride(player, stack);
        }
    }

    public static void onItemUsed(ServerPlayer player, ItemStack stack) {
        if (!player.isUsingItem()) {
            applyCooldownOverride(player, stack);
        }
    }

    private static void applyCooldownOverride(ServerPlayer player, ItemStack stack) {
        int cooldown = stack.getOrDefault(ExtraDataComponents.COOLDOWN_OVERRIDE, 0);
        if (cooldown != 0) {
            player.getCooldowns().addCooldown(stack, cooldown);
        }
    }

    @SubscribeEvent
    public static void onItemFinishedUsing(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ItemStack stack = event.getItem();
            applyCooldownOverride(player, stack);

            if(stack.has(ExtraDataComponents.TRANSPORT_TO_DIMENSION) && event.getEntity().level() instanceof ServerLevel serverLevel){
                if (stack.has(DataComponents.CONSUMABLE) && stack.has(DataComponents.MAX_DAMAGE)) {
                    stack.hurtAndBreak(1, serverLevel, null, _ -> {});
                }
                TransportToDimensionData transportToDimensionData = stack.get(ExtraDataComponents.TRANSPORT_TO_DIMENSION);
                if(transportToDimensionData != null) {
                    ServerLevel level = serverLevel.getServer().getLevel(transportToDimensionData.destinationLevel());
                    TransportToDimensionData transportToDimensionData1 = new TransportToDimensionData(
                            player.level().dimension(), Optional.of(player.position()));
                    player.teleport(new TeleportTransition(
                            level,
                            transportToDimensionData.destinationPos().orElseThrow(),
                            player.getDeltaMovement(),
                            player.getYRot(), player.getXRot(), TeleportTransition.DO_NOTHING));
                    stack.set(ExtraDataComponents.TRANSPORT_TO_DIMENSION, transportToDimensionData1);
                }
                event.setResultStack(stack);
                return;
            }

            InteractActionData actionData = stack.get(ExtraDataComponents.INTERACT_ACTION);
            if (actionData != null) {
                actionData.performCommands(player);
            }
        }
    }

    @SubscribeEvent
    public static void onItemStoppedUsing(LivingEntityUseItemEvent.Stop event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ItemStack stack = event.getItem();
            // Don't apply cooldown when loading projectiles into a crossbow, only on fire
            if (!stack.getOrDefault(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY).isEmpty()) {
                return;
            }
            applyCooldownOverride(player, stack);
        }
    }

    @SubscribeEvent
    public static void onInteract(PlayerInteractEvent.RightClickItem event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ItemStack itemStack = event.getItemStack();
            // Food is handled above in LivingEntityUseItemEvent.Finish
            if (itemStack.has(DataComponents.CONSUMABLE)) {
                return;
            }
            InteractActionData actionData = itemStack.get(ExtraDataComponents.INTERACT_ACTION);
            if (actionData != null) {
                if (actionData.cancelEvent()) {
                    event.setCanceled(true);
                }
                actionData.performCommands(player);
            }
        }
    }
}
