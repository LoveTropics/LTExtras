package com.lovetropics.extras.mixin.translation;

import net.minecraft.util.FutureChain;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.Executor;

@Mixin(FutureChain.class)
public class FutureChainMixin {
    @Shadow
    @Final
    @Mutable
    private Executor checkedExecutor;
    @Shadow
    private volatile boolean closed;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(final Executor executor, final CallbackInfo ci) {
        // Fix for a race condition that we're exposing when a player disconnects
        checkedExecutor = task -> {
            if (!closed) {
                executor.execute(() -> {
                    if (!closed) {
                        task.run();
                    }
                });
            }
        };
    }
}
