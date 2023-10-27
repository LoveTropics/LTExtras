package com.lovetropics.extras.client;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.client.screen.map.TropicalMapScreen;
import com.lovetropics.extras.data.poi.Poi;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

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
}
