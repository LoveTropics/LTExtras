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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = LTExtras.MODID, value = Dist.CLIENT)
public class ClientMapPoiManager {
    private static final Map<String, Poi> POIS = new HashMap<>();

    public static void updatePoi(final Poi poi, boolean delete) {
        if (delete) {
            POIS.remove(poi.name());
        } else {
            POIS.put(poi.name(), poi);
        }
    }

    public static Map<String, Poi> getPois() {
        return POIS;
    }

    public static void openScreen(final Player player) {
        Minecraft.getInstance().setScreen(new TropicalMapScreen(Component.translatable("item.ltextras.tropical_map"), player));
    }

    @SubscribeEvent
    public static void onLoggingOut(final ClientPlayerNetworkEvent.LoggingOut event) {
        POIS.clear();
    }

    public static ResourceLocation getFace(final UUID uuid) {
        return getPlayerSkinOrDefault(uuid);
    }

    private static ResourceLocation getPlayerSkinOrDefault(final UUID uuid) {
        final ClientPacketListener connection = Minecraft.getInstance().getConnection();

        final PlayerInfo playerInfo;
        if (connection == null || (playerInfo = connection.getPlayerInfo(uuid)) == null) {
            return DefaultPlayerSkin.getDefaultSkin(uuid);
        }
        return playerInfo.getSkinLocation();
    }
}
