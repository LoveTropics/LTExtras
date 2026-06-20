package com.lovetropics.extras.entity;

import com.lovetropics.extras.ExtraTags;
import com.lovetropics.extras.data.attachment.ExtraAttachments;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber
public class EntityInteractionEvents {

    @SubscribeEvent
    public static void getHonied(PlayerInteractEvent.EntityInteract interactEvent) {
        final Entity target = interactEvent.getTarget();
        final ItemStack itemUsed = interactEvent.getItemStack();

        if (target.getType() == EntityTypes.SHULKER && itemUsed.is(ExtraTags.Items.HONIES)) {
            target.setData(ExtraAttachments.HONIED, true);
        }
    }
}
