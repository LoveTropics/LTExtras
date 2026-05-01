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

@EventBusSubscriber(modid = LTExtras.MODID)
public class WorldEffectConfigs {
    public static final CodecRegistry<Identifier, Named<WorldEffect>> REGISTRY = CodecRegistry.resourceLocationKeys();
    private static final SimpleDataPackLister<WorldEffect> LISTER = new SimpleDataPackLister<>("world_effects", ExtraRegistries.WORLD_EFFECT, WorldEffect.CODEC);

    @SubscribeEvent
    public static void addReloadListener(AddServerReloadListenersEvent event) {
        RegistryAccess registries = event.getRegistryAccess();
        event.addListener(LTExtras.location("world_effects"), (barrier, resourceManager, backgroundExecutor, gameExecutor) ->
                LISTER.load(registries, barrier.resourceManager(), resourceManager)
                        .thenCompose(backgroundExecutor::wait)
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
