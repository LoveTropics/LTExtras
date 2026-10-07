package com.lovetropics.extras.mixin;

import com.lovetropics.extras.ExtraStats;
import com.lovetropics.extras.zipline.ZiplineRider;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {
    // Count riding a zipline separately, rather than as distance flown
    @Inject(method = "checkMovementStatistics", at = @At("HEAD"), cancellable = true)
    private void checkZiplineStatistics(double dx, double dy, double dz, CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        if (ZiplineRider.isRiding(player)) {
            int distance = Math.round((float) Math.sqrt(dx * dx + dy * dy + dz * dz) * 100.0f);
            if (distance > 0) {
                player.awardStat(ExtraStats.ZIPLINE_ONE_CM.get(), distance);
            }
            ci.cancel();
        }
    }
}
