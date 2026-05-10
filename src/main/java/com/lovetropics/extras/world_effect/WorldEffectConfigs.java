package com.lovetropics.extras.world_effect;

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
public class WorldEffectConfigs {
    public static final CodecRegistry<Identifier, Named<WorldEffect>> REGISTRY = CodecRegistry.idKeys();
    private static final SimpleDataPackLister<WorldEffect> LISTER = new SimpleDataPackLister<>("world_effects", ExtraRegistries.WORLD_EFFECT, WorldEffect.CODEC);

    @SubscribeEvent
    public static void addReloadListener(AddServerReloadListenersEvent event) {
        RegistryAccess registries = event.getRegistryAccess();
        event.addListener(LTExtras.id("world_effects"), (currentReload, taskExecutor, barrier, reloadExecutor) ->
                LISTER.load(registries, currentReload.resourceManager(), taskExecutor)
                        .thenCompose(barrier::wait)
                        .thenAcceptAsync(effects -> {
                            REGISTRY.clear();
                            effects.forEach(holder ->
                                    REGISTRY.register(holder.id(), holder)
                            );
                            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
                            if (server != null) {
                                WorldEffectManager.reload(server);
                            }
                        }, reloadExecutor)
        );
    }
}
