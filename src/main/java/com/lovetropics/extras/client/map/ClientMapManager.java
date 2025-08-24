package com.lovetropics.extras.client.map;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.client.screen.map.TropicalMapScreen;
import com.lovetropics.extras.data.poi.MapConfig;
import com.lovetropics.extras.data.poi.PoiConfig;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@EventBusSubscriber(modid = LTExtras.MODID, value = Dist.CLIENT)
public class ClientMapManager {
	private static final Int2ObjectMap<ClientPoi> POIS = new Int2ObjectOpenHashMap<>();

	public static void updatePoi(int networkId, ResourceKey<PoiConfig> id, Holder<MapConfig> map, Component description, PoiConfig.Icon icon, int markerX, int markerY) {
		POIS.put(networkId, new ClientPoi(id, map, description, icon, markerX, markerY));
	}

	public static void removePoi(int networkId) {
		POIS.remove(networkId);
	}

	public static void updateFaces(int networkId, List<UUID> faces) {
		ClientPoi poi = POIS.get(networkId);
		if (poi != null) {
			poi.updateFaces(faces);
		}
	}

	public static void openScreen(Player player, Holder<MapConfig> map) {
		List<ClientPoi> pois = POIS.values().stream().filter(poi -> poi.map().equals(map)).toList();
		Minecraft.getInstance().setScreen(new TropicalMapScreen(player, map.value(), pois));
	}

	@SubscribeEvent
	public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
		POIS.clear();
	}

	public static PlayerSkin getOnlinePlayerSkin(UUID id) {
		ClientPacketListener connection = Minecraft.getInstance().getConnection();
		PlayerInfo playerInfo;
		if (connection == null || (playerInfo = connection.getPlayerInfo(id)) == null) {
			return DefaultPlayerSkin.get(id);
		}
		return playerInfo.getSkin();
	}

	public static Collection<ClientPoi> get() {
		return POIS.values();
	}
}
