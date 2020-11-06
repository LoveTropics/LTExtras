package com.lovetropics.extras.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.DimensionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DimensionManager.class)
public class DimensionManagerMixin {
	private static long lastPrintTime;

	@Inject(method = "getWorld", at = @At(value = "FIELD", target = "Lnet/minecraftforge/common/DimensionManager$Data;ticksWaited:I"))
	private static void resetTicksWaiting(MinecraftServer server, DimensionType dim, boolean resetUnloadDelay, boolean forceLoad, CallbackInfoReturnable<ServerWorld> ci) {
		if (dim.getRegistryName().getPath().equals("signature_run_game")) {
			long time = server.getTickCounter();
			if (time - lastPrintTime > 20 * 5) {
				new Exception().printStackTrace();
				lastPrintTime = time;
			}
		}
	}
}
