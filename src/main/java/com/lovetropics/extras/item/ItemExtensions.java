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
    public static boolean onItemToss(final Player player, final ItemEntity item) {
        final ItemStack stack = item.getItem();
        if (stack.has(ExtraDataComponents.UNDROPPABLE)) {
            player.addItem(stack);
            return true;
        }
        return false;
    }

    @SubscribeEvent
    public static void onItemPickup(final ItemEntityPickupEvent.Pre event) {
        final ItemStack stack = event.getItemEntity().getItem();
        if (stack.has(ExtraDataComponents.UNDROPPABLE)) {
            stack.setCount(0);
            event.getItemEntity().discard();
            event.setCanPickup(TriState.FALSE);
        }
    }

    @SubscribeEvent
    public static void onLivingDrops(final LivingDropsEvent event) {
        event.getDrops().removeIf(item -> item.getItem().has(ExtraDataComponents.UNDROPPABLE));
    }

    public static void onItemUsedOn(final ServerPlayer player, final ItemStack stack, final UseOnContext context) {
        applyCooldownOverride(player, stack);
    }

    public static void onItemUsed(final ServerPlayer player, final ItemStack stack) {
        applyCooldownOverride(player, stack);
    }

    private static void applyCooldownOverride(final ServerPlayer player, final ItemStack stack) {
        if (player.isUsingItem()) {
            return;
        }
        final int cooldown = stack.getOrDefault(ExtraDataComponents.COOLDOWN_OVERRIDE, 0);
        if (cooldown != 0) {
            player.getCooldowns().addCooldown(stack.getItem(), cooldown);
        }
    }

    @SubscribeEvent
    public static void onItemFinishedUsing(final LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity() instanceof final ServerPlayer player) {
            final ItemStack stack = event.getItem();
            final int cooldown = stack.getOrDefault(ExtraDataComponents.COOLDOWN_OVERRIDE, 0);
            if (cooldown != 0) {
                player.getCooldowns().addCooldown(event.getItem().getItem(), cooldown);
            }
        }
    }
}
