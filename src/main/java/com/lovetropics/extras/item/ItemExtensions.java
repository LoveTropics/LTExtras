package com.lovetropics.extras.item;

import com.lovetropics.extras.LTExtras;
import net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = LTExtras.MODID)
public class ItemExtensions {
    private static final String TAG_UNDROPPABLE = "undroppable";
    private static final String TAG_COOLDOWN_OVERRIDE = "cooldown_override";

    private static final int NO_COOLDOWN_OVERRIDE = 0;

    public static boolean onItemToss(final Player player, final ItemEntity item) {
        final ItemStack stack = item.getItem();
        if (isUndroppable(stack)) {
            player.addItem(stack);
            return true;
        }
        return false;
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

    public static void onItemUsed(final ServerPlayer player, final ItemStack stack) {
        if (player.isUsingItem()) {
            return;
        }
        final int cooldown = getCooldown(stack);
        if (cooldown != NO_COOLDOWN_OVERRIDE) {
            player.getCooldowns().addCooldown(stack.getItem(), cooldown);
        }
    }

    @SubscribeEvent
    public static void onItemFinishedUsing(final LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity() instanceof final ServerPlayer player) {
            final int cooldown = getCooldown(event.getItem());
            if (cooldown != NO_COOLDOWN_OVERRIDE) {
                player.getCooldowns().addCooldown(event.getItem().getItem(), cooldown);
            }
        }
    }

    private static boolean isUndroppable(final ItemStack stack) {
        return stack.getTag() != null && stack.getTag().contains(TAG_UNDROPPABLE);
    }

    private static int getCooldown(final ItemStack stack) {
        if (stack.getTag() != null) {
            return stack.getTag().getInt(TAG_COOLDOWN_OVERRIDE);
        }
        return NO_COOLDOWN_OVERRIDE;
    }
}
