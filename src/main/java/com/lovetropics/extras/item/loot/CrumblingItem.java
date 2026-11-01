package com.lovetropics.extras.item.loot;

import com.lovetropics.extras.ExtraDataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

/**
 * LTExtras
 * FIRE EMOJI, FIRE EMOJI, FIRE EMOJI
 */
public class CrumblingItem extends Item {
    public CrumblingItem(Properties properties) {
        super(properties);
    }

    private int nextCrumbleTick = 0;

    @Override
    public void inventoryTick(ItemStack itemStack, ServerLevel level, Entity owner, @Nullable EquipmentSlot slot) {
        super.inventoryTick(itemStack, level, owner, slot);
        CrumblingItemData crumbleData = itemStack.getOrDefault(ExtraDataComponents.CRUMBLING_ITEM, CrumblingItemData.DEFAULT);
        if (!itemStack.isDamageableItem() ||  !(owner instanceof LivingEntity livingEntity) || (slot == null && !crumbleData.inNonEquipmentSlot()) || !crumbleData.inEquipmentSlots().contains(slot)) {
            return;
        }

        if (nextCrumbleTick > crumbleData.intervalTicks()) {
            nextCrumbleTick = 0;
            if (slot == null) {
                itemStack.hurtAndBreak(crumbleData.damageAmount(), level, livingEntity, (item) -> {});
            } else {
                itemStack.hurtAndBreak(crumbleData.damageAmount(), livingEntity, slot);
            }
        } else {
            nextCrumbleTick++;
        }

    }
}
