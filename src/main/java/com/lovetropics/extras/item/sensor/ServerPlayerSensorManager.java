package com.lovetropics.extras.item.sensor;

import com.lovetropics.extras.ExtraDataComponents;
import com.lovetropics.extras.ExtraTags;
import com.lovetropics.extras.network.message.ClientboundSetEntityMarkedPacket;
import com.lovetropics.extras.registry.ExtraRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@EventBusSubscriber
public class ServerPlayerSensorManager {
    private static final int REFRESH_INTERVAL_TICKS = 5;

    private static final Map<UUID, SensorState> SENSOR_STATES = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        SENSOR_STATES.remove(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (player.tickCount % REFRESH_INTERVAL_TICKS != 0) {
                return;
            }
            SensorState sensorState = SENSOR_STATES.get(player.getUUID());
            if (sensorState != null) {
                sensorState.refresh(player, collectActiveSensors(player));
            }
        }
    }

    private static List<Holder<PlayerSensor>> collectActiveSensors(ServerPlayer player) {
        List<Holder<PlayerSensor>> sensors = new ArrayList<>();

        ItemStack headItem = player.getItemBySlot(EquipmentSlot.HEAD);
        Holder<PlayerSensor> sensor = headItem.get(ExtraDataComponents.PLAYER_SENSOR);
        if (sensor != null) {
            sensors.add(sensor);
        }

        Registry<PlayerSensor> sensorRegistry = player.registryAccess().lookupOrThrow(ExtraRegistries.PLAYER_SENSOR);
        for (Holder<PlayerSensor> baseSensor : sensorRegistry.getTagOrEmpty(ExtraTags.PlayerSensors.ALWAYS_VISIBLE)) {
            sensors.add(baseSensor);
        }

        return sensors;
    }

    @SubscribeEvent
    public static void onPlayerTracked(PlayerEvent.StartTracking event) {
        if (event.getEntity() instanceof ServerPlayer player && event.getTarget() instanceof ServerPlayer target) {
            SensorState state = SENSOR_STATES.computeIfAbsent(player.getUUID(), playerId -> new SensorState());
            state.startTracking(player, target);
        }
    }

    @SubscribeEvent
    public static void onPlayerUntracked(PlayerEvent.StopTracking event) {
        if (event.getEntity() instanceof ServerPlayer player && event.getTarget() instanceof ServerPlayer target) {
            SensorState state = SENSOR_STATES.get(player.getUUID());
            if (state != null) {
                state.stopTracking(target);
            }
        }
    }

    private static class SensorState {
        private List<Holder<PlayerSensor>> activeSensors = List.of();
        private final Set<UUID> trackedPlayers = new HashSet<>();
        private final Map<UUID, PlayerSensor.Appearance> markedPlayers = new HashMap<>();

        public void refresh(ServerPlayer player, List<Holder<PlayerSensor>> sensors) {
            activeSensors = sensors;

            ServerLevel level = player.level();

            Iterator<Map.Entry<UUID, PlayerSensor.Appearance>> iterator = markedPlayers.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<UUID, PlayerSensor.Appearance> entry = iterator.next();
                UUID playerId = entry.getKey();
                if (!(level.getPlayerByUUID(playerId) instanceof ServerPlayer target)) {
                    // Shouldn't get here - but the client probably forgot about the player if we did too
                    iterator.remove();
                    continue;
                }

                PlayerSensor.Appearance newMatching = selectMatching(target);
                if (entry.getValue().equals(newMatching)) {
                    continue;
                }
                if (newMatching != null) {
                    entry.setValue(newMatching);
                } else {
                    iterator.remove();
                }
                player.connection.send(new ClientboundSetEntityMarkedPacket(target.getId(), Optional.ofNullable(newMatching)));
            }

            for (UUID playerId : trackedPlayers) {
                if (markedPlayers.containsKey(playerId)) {
                    continue;
                }
                if (level.getPlayerByUUID(playerId) instanceof ServerPlayer target) {
                    PlayerSensor.Appearance newMatching = selectMatching(target);
                    if (newMatching != null) {
                        markedPlayers.put(playerId, newMatching);
                        player.connection.send(new ClientboundSetEntityMarkedPacket(target.getId(), Optional.of(newMatching)));
                    }
                }
            }
        }

        private PlayerSensor.@Nullable Appearance selectMatching(ServerPlayer target) {
            for (Holder<PlayerSensor> sensor : activeSensors) {
                if (sensor.value().matches(target)) {
                    return sensor.value().appearance();
                }
            }
            return null;
        }

        public void startTracking(ServerPlayer player, ServerPlayer target) {
            trackedPlayers.add(target.getUUID());
            refresh(player, activeSensors);
        }

        public void stopTracking(ServerPlayer target) {
            // Client will forget the entity, we don't need to send anything
            trackedPlayers.remove(target.getUUID());
            markedPlayers.remove(target.getUUID());
        }
    }
}
