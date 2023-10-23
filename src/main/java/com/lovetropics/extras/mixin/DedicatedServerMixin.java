package com.lovetropics.extras.mixin;

import net.minecraft.server.dedicated.DedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;

@Mixin(value = DedicatedServer.class, priority = 2000)
public class DedicatedServerMixin {
    // Hackfix: some mods keep non-daemon threads running after server exit.
    // Forcefully shut down the process if we fail to shut down within 3 seconds.
    @Inject(method = "onServerExit", at = @At("RETURN"))
    private void onServerExit(final CallbackInfo ci) {
        final Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.exit(0);
            }
        }, Duration.ofSeconds(3).toMillis());
    }
}
