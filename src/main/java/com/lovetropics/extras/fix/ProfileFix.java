package com.lovetropics.extras.fix;

import com.lovetropics.extras.duck.ExtendedProfileCache;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/// Minecraft services are proving unreliable - we can avoid a bunch of requests that could fail by populating our cache with players who join.
@EventBusSubscriber
public class ProfileFix {
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && player.level().getServer().services().profileResolver() instanceof ExtendedProfileCache cache) {
            cache.lt$insertFromJoin(player.getGameProfile());
        }
    }
}
