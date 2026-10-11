package com.lovetropics.extras.zipline;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber
public final class ZiplineEvents {
    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity().getVehicle() instanceof ZiplineRideEntity) {
            // Preserve the current hanging position rather than relocating the player on logout.
            event.getEntity().stopRiding();
        }
    }
}
