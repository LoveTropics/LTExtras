package com.lovetropics.extras.item.sensor;

import com.lovetropics.extras.ExtraDataComponents;
import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.network.message.ClientboundSetEntityMarkedPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@EventBusSubscriber(modid = LTExtras.MODID)
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
			ItemStack headItem = player.getItemBySlot(EquipmentSlot.HEAD);
			PlayerSensor sensor = headItem.get(ExtraDataComponents.PLAYER_SENSOR);
			if (sensor != null) {
				SensorState sensorState = SENSOR_STATES.computeIfAbsent(player.getUUID(), playerId -> new SensorState(sensor));
				sensorState.activeSensor = sensor;
				if (player.tickCount % REFRESH_INTERVAL_TICKS == 0) {
					sensorState.refresh(player);
				}
			} else {
				SensorState sensorState = SENSOR_STATES.remove(player.getUUID());
				if (sensorState != null) {
					sensorState.clearAllMarked(player);
				}
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerTracked(PlayerEvent.StartTracking event) {
		if (event.getEntity() instanceof ServerPlayer player && event.getTarget() instanceof ServerPlayer target) {
			SensorState state = SENSOR_STATES.get(player.getUUID());
			if (state != null) {
				state.startTracking(player, target);
			}
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
		private PlayerSensor activeSensor;
		private final Set<UUID> trackedPlayers = new HashSet<>();
		private final Set<UUID> markedPlayers = new HashSet<>();

		private SensorState(PlayerSensor activeSensor) {
			this.activeSensor = activeSensor;
		}

		public void refresh(ServerPlayer player) {
			ServerLevel level = player.serverLevel();

			markedPlayers.removeIf(playerId -> {
				if (!(level.getPlayerByUUID(playerId) instanceof ServerPlayer target)) {
					// Shouldn't get here - but the client probably forgot about the player if we did too
					return true;
				}
				if (!activeSensor.matches(target)) {
					player.connection.send(new ClientboundSetEntityMarkedPacket(target.getId(), Optional.empty()));
					return true;
				}
				return false;
			});

			for (UUID playerId : trackedPlayers) {
				if (markedPlayers.contains(playerId)) {
					continue;
				}
				if (level.getPlayerByUUID(playerId) instanceof ServerPlayer target && activeSensor.matches(target)) {
					markedPlayers.add(playerId);
					player.connection.send(new ClientboundSetEntityMarkedPacket(target.getId(), Optional.of(activeSensor.appearance())));
				}
			}
		}

		public void startTracking(ServerPlayer player, ServerPlayer target) {
			trackedPlayers.add(target.getUUID());
			refresh(player);
		}

		public void stopTracking(ServerPlayer target) {
			// Client will forget the entity, we don't need to send anything
			trackedPlayers.remove(target.getUUID());
			markedPlayers.remove(target.getUUID());
		}

		public void clearAllMarked(ServerPlayer player) {
			for (UUID playerId : markedPlayers) {
				if (player.level().getPlayerByUUID(playerId) instanceof ServerPlayer target) {
					player.connection.send(new ClientboundSetEntityMarkedPacket(target.getId(), Optional.empty()));
				}
			}
			markedPlayers.clear();
		}
	}
}
