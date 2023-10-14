package com.lovetropics.extras.schedule;

import com.lovetropics.extras.LTExtras;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.time.ZoneId;
import java.time.ZoneOffset;

@Mod.EventBusSubscriber(modid = LTExtras.MODID)
public class PlayerTimeZone implements ICapabilityProvider {
    public static final ResourceLocation ID = new ResourceLocation(LTExtras.MODID, "time_zone");

    private final LazyOptional<PlayerTimeZone> instance = LazyOptional.of(() -> this);

    private ZoneId zoneId = ZoneOffset.UTC;

    @SubscribeEvent
    public static void onAttachEntityCapabilities(final AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof ServerPlayer) {
            event.addCapability(ID, new PlayerTimeZone());
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(final PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            final PlayerTimeZone oldTimeZone = getOrNull(event.getOriginal());
            final PlayerTimeZone newTimeZone = getOrNull(event.getEntity());
            if (oldTimeZone != null && newTimeZone != null) {
                newTimeZone.zoneId = oldTimeZone.zoneId;
            }
        }
    }

    public static void set(final ServerPlayer player, final ZoneId zone) {
        final PlayerTimeZone capability = getOrNull(player);
        if (capability != null) {
            capability.zoneId = zone;
        }
    }

    public static ZoneId get(final ServerPlayer player) {
        final PlayerTimeZone capability = getOrNull(player);
        return capability != null ? capability.zoneId : ZoneOffset.UTC;
    }

    @Nullable
    private static PlayerTimeZone getOrNull(final Player player) {
        return player.getCapability(LTExtras.PLAYER_TIME_ZONE).orElse(null);
    }

    @Override
    public <T> LazyOptional<T> getCapability(final Capability<T> cap, @Nullable final Direction side) {
        return LTExtras.PLAYER_TIME_ZONE.orEmpty(cap, instance);
    }
}
