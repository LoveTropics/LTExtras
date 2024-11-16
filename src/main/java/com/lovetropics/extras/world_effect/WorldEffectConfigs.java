package com.lovetropics.extras.world_effect;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.data.Named;
import com.lovetropics.extras.data.SimpleDataPackLister;
import com.lovetropics.lib.codec.CodecRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

@EventBusSubscriber(modid = LTExtras.MODID)
public class WorldEffectConfigs {
    public static final CodecRegistry<ResourceLocation, Named<WorldEffect>> REGISTRY = CodecRegistry.resourceLocationKeys();
    private static final SimpleDataPackLister<WorldEffect> LISTER = new SimpleDataPackLister<>("world_effects", WorldEffect.CODEC);

    @SubscribeEvent
    public static void addReloadListener(AddReloadListenerEvent event) {
        RegistryAccess registries = event.getRegistryAccess();
        event.addListener((stage, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor) ->
                LISTER.load(registries, resourceManager, backgroundExecutor)
                        .thenCompose(stage::wait)
                        .thenAcceptAsync(effects -> {
                            REGISTRY.clear();
                            effects.forEach(holder ->
                                    REGISTRY.register(holder.id(), holder)
                            );
                            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
                            if (server != null) {
                                WorldEffectManager.reload(server);
                            }
                        }, gameExecutor)
        );
    }
}
