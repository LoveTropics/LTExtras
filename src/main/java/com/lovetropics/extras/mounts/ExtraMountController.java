package com.lovetropics.extras.mounts;

import com.lovetropics.extras.data.attachment.ExtraAttachments;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityMountEvent;

@EventBusSubscriber
public class ExtraMountController {

    @SubscribeEvent
    public static void onEntityMount(EntityMountEvent event) {
        if (event.getEntityBeingMounted().isRemoved() || !event.isDismounting() || !event.getEntityBeingMounted().hasData(ExtraAttachments.MOUNT)) {
            return;
        }

        event.getEntityBeingMounted().remove(Entity.RemovalReason.DISCARDED);
    }
}
