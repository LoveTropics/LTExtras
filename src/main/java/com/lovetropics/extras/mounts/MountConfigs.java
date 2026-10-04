package com.lovetropics.extras.mounts;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.data.Named;
import com.lovetropics.extras.data.SimpleDataPackLister;
import com.lovetropics.extras.registry.ExtraRegistries;
import com.lovetropics.lib.codec.CodecRegistry;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.resource.ContextAwareReloadListener;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@EventBusSubscriber
public class MountConfigs {
    public static final CodecRegistry<Identifier, Named<Mount>> MOUNTS = CodecRegistry.idKeys();

    private static final SimpleDataPackLister<Mount> MOUNT_LISTENER = new SimpleDataPackLister<>("ltextras/mounts", ExtraRegistries.MOUNT, Mount.MAP_CODEC);

    @SubscribeEvent
    public static void addReloadListener(AddServerReloadListenersEvent event) {
        event.addListener(LTExtras.id("mounts"), new ContextAwareReloadListener() {
            @Override
            public CompletableFuture<Void> reload(SharedState currentReload, Executor taskExecutor, PreparationBarrier barrier, Executor reloadExecutor) {
                return MOUNT_LISTENER.load(getRegistryLookup(), currentReload.resourceManager(), taskExecutor)
                        .thenCompose(barrier::wait)
                        .thenAcceptAsync(mounts -> {
                            MOUNTS.clear();
                            mounts.forEach(holder ->
                                    MOUNTS.register(holder.id(), holder));
                        }, reloadExecutor);
            }
        });
    }
}
