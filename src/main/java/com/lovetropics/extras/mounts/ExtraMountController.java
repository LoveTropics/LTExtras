package com.lovetropics.extras.mounts;

import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityMountEvent;

@EventBusSubscriber
public class ExtraMountController {

    public static final String KILL_DISMOUNT = "remove_on_dismount";

    @SubscribeEvent
    public static void onEntityMount(EntityMountEvent event) {
        if (event.getEntityBeingMounted().isRemoved() || !event.isDismounting() || !event.getEntityBeingMounted().getTags().contains(KILL_DISMOUNT)) {
            return;
        }

        event.getEntityBeingMounted().remove(Entity.RemovalReason.DISCARDED);
    }
}
