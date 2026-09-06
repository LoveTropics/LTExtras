package com.lovetropics.extras.environmentattribute;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.data.Named;
import com.lovetropics.extras.data.SimpleDataPackLister;
import com.lovetropics.extras.registry.ExtraRegistries;
import com.lovetropics.lib.codec.CodecRegistry;
import net.minecraft.resources.Identifier;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.resource.ContextAwareReloadListener;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@EventBusSubscriber
public class EnvironmentAttributeBundleConfigs {
    public static final CodecRegistry<Identifier, Named<EnvironmentAttributeMap>> REGISTRY = CodecRegistry.idKeys();
    private static final SimpleDataPackLister<EnvironmentAttributeMap> LISTER = new SimpleDataPackLister<>("environment_attribute_bundle", ExtraRegistries.ENVIRONMENT_ATTRIBUTE_BUNDLE, EnvironmentAttributeMap.CODEC);

    @SubscribeEvent
    public static void addReloadListener(AddServerReloadListenersEvent event) {
        event.addListener(LTExtras.id("environment_attribute_bundles"), new ContextAwareReloadListener() {
            @Override
            public CompletableFuture<Void> reload(SharedState currentReload, Executor taskExecutor, PreparationBarrier barrier, Executor reloadExecutor) {
                return LISTER.load(getRegistryLookup(), currentReload.resourceManager(), taskExecutor)
                        .thenCompose(barrier::wait)
                        .thenAcceptAsync(bundles -> {
                            REGISTRY.clear();
                            bundles.forEach(holder ->
                                    REGISTRY.register(holder.id(), holder)
                            );
                        }, reloadExecutor);
            }
        });
    }
}
