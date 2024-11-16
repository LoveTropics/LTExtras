package com.lovetropics.extras.data.poi;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.data.Named;
import com.lovetropics.extras.data.SimpleDataPackLister;
import com.lovetropics.extras.registry.ExtraRegistries;
import com.lovetropics.lib.codec.CodecRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

@EventBusSubscriber(modid = LTExtras.MODID)
public class MapConfigs {
	public static final CodecRegistry<ResourceLocation, Named<PoiConfig>> POIS = CodecRegistry.resourceLocationKeys();

	private static final SimpleDataPackLister<PoiConfig> POI_LISTER = new SimpleDataPackLister<>("ltextras/map_poi", ExtraRegistries.POI, PoiConfig.CODEC);

	@SubscribeEvent
	public static void addReloadListener(AddReloadListenerEvent event) {
		RegistryAccess registries = event.getRegistryAccess();
		event.addListener((stage, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor) ->
				POI_LISTER.load(registries, resourceManager, backgroundExecutor)
						.thenCompose(stage::wait)
						.thenAcceptAsync(pois -> {
							POIS.clear();
							pois.forEach(holder -> POIS.register(holder.id(), holder));

							MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
							if (server != null) {
								MapManager.get(server).reload(server);
							}
						}, gameExecutor));
	}
}
