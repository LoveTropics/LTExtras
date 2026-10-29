package com.lovetropics.extras.registry;

import com.lovetropics.extras.data.Named;
import com.lovetropics.extras.data.SimpleDataPackLister;
import com.lovetropics.lib.codec.CodecRegistry;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.resource.ContextAwareReloadListener;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class CustomRegistryReloadListener<T> extends ContextAwareReloadListener {

    private final SimpleDataPackLister<T> dataPackLister;
    private final CodecRegistry<Identifier, Named<T>> registry;

    protected CustomRegistryReloadListener(SimpleDataPackLister<T> dataPackLister, CodecRegistry<Identifier, Named<T>> registry) {
        this.dataPackLister = dataPackLister;
        this.registry = registry;
    }

    public static <T> CustomRegistryReloadListener<T> of(SimpleDataPackLister<T> dataPackLister, CodecRegistry<Identifier, Named<T>> registry) {
        return new CustomRegistryReloadListener<>(dataPackLister, registry);
    }

    @Override
    public CompletableFuture<Void> reload(SharedState currentReload, Executor taskExecutor, PreparationBarrier barrier, Executor reloadExecutor) {
        return dataPackLister.load(getRegistryLookup(), currentReload.resourceManager(), taskExecutor)
                .thenCompose(barrier::wait)
                .thenAcceptAsync(mounts -> {
                    registry.clear();
                    mounts.forEach(holder ->
                            registry.register(holder.id(), holder));
                }, reloadExecutor);
    }
}
