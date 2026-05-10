package com.lovetropics.extras.data.poi;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.data.Named;
import com.lovetropics.extras.data.SimpleDataPackLister;
import com.lovetropics.extras.registry.ExtraRegistries;
import com.lovetropics.lib.codec.CodecRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

@EventBusSubscriber
public class MapConfigs {
    public static final CodecRegistry<Identifier, Named<PoiConfig>> POIS = CodecRegistry.idKeys();

    private static final SimpleDataPackLister<PoiConfig> POI_LISTER = new SimpleDataPackLister<>("ltextras/map_poi", ExtraRegistries.POI, PoiConfig.CODEC);

    @SubscribeEvent
    public static void addReloadListener(AddServerReloadListenersEvent event) {
        RegistryAccess registries = event.getRegistryAccess();
        event.addListener(LTExtras.id("map_configs"), (currentReload, taskExecutor, barrier, reloadExecutor) ->
                POI_LISTER.load(registries, currentReload.resourceManager(), taskExecutor)
                        .thenCompose(barrier::wait)
                        .thenAcceptAsync(pois -> {
                            POIS.clear();
                            pois.forEach(holder -> POIS.register(holder.id(), holder));

                            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
                            if (server != null) {
                                MapManager.get(server).reload(server);
                            }
                        }, reloadExecutor)
        );
    }
}
