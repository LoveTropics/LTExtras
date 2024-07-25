package com.lovetropics.extras.item;

import com.lovetropics.extras.ExtraDataComponents;
import com.lovetropics.extras.LTExtras;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;

@EventBusSubscriber(modid = LTExtras.MODID)
public class ItemExtensions {
    public static boolean onItemToss(Player player, ItemEntity item) {
        ItemStack stack = item.getItem();
        if (stack.has(ExtraDataComponents.UNDROPPABLE)) {
            player.addItem(stack);
            return true;
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
        }
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        event.getDrops().removeIf(item -> item.getItem().has(ExtraDataComponents.UNDROPPABLE));
    }

    public static void onItemUsedOn(ServerPlayer player, ItemStack stack, UseOnContext context) {
        applyCooldownOverride(player, stack);
    }

    public static void onItemUsed(ServerPlayer player, ItemStack stack) {
        applyCooldownOverride(player, stack);
    }

    private static void applyCooldownOverride(ServerPlayer player, ItemStack stack) {
        if (player.isUsingItem()) {
            return;
        }
        int cooldown = stack.getOrDefault(ExtraDataComponents.COOLDOWN_OVERRIDE, 0);
        if (cooldown != 0) {
            player.getCooldowns().addCooldown(stack.getItem(), cooldown);
        }
    }

    @SubscribeEvent
    public static void onItemFinishedUsing(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ItemStack stack = event.getItem();
            int cooldown = stack.getOrDefault(ExtraDataComponents.COOLDOWN_OVERRIDE, 0);
            if (cooldown != 0) {
                player.getCooldowns().addCooldown(event.getItem().getItem(), cooldown);
            }
        }
    }
}
