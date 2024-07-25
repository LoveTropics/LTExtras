package com.lovetropics.extras.client;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.client.screen.map.TropicalMapScreen;
import com.lovetropics.extras.data.poi.Poi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = LTExtras.MODID, value = Dist.CLIENT)
public class ClientMapPoiManager {
    private static final Map<String, Poi> POIS = new HashMap<>();

    public static void updatePoi(Poi poi, boolean delete) {
        if (delete) {
            POIS.remove(poi.name());
        } else {
            POIS.put(poi.name(), poi);
        }
    }

    public static Map<String, Poi> getPois() {
        return POIS;
    }

    public static void openScreen(Player player) {
        Minecraft.getInstance().setScreen(new TropicalMapScreen(Component.translatable("item.ltextras.tropical_map"), player));
    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        POIS.clear();
    }

    public static ResourceLocation getFace(UUID uuid) {
        return getPlayerSkinOrDefault(uuid);
    }

    private static ResourceLocation getPlayerSkinOrDefault(UUID uuid) {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();

        PlayerInfo playerInfo;
        if (connection == null || (playerInfo = connection.getPlayerInfo(uuid)) == null) {
            return DefaultPlayerSkin.get(uuid).texture();
        }
        return playerInfo.getSkin().texture();
    }
}
