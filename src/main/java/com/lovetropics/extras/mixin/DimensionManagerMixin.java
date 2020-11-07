package com.lovetropics.extras.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.DimensionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = DimensionManager.class, remap = false)
public class DimensionManagerMixin {
	private static final Logger LOGGER = LogManager.getLogger("LTExtrasDebug");

	private static long lastPrintTime;

	@Inject(method = "getWorld", at = @At(value = "FIELD", target = "Lnet/minecraftforge/common/DimensionManager$Data;ticksWaited:I", remap = false), remap = false)
	private static void resetTicksWaiting(MinecraftServer server, DimensionType dim, boolean resetUnloadDelay, boolean forceLoad, CallbackInfoReturnable<ServerWorld> ci) {
		if (dim.getRegistryName().getPath().equals("signature_run_game") && resetUnloadDelay) {
			long time = server.getTickCounter();
			if (time - lastPrintTime > 20 * 5) {
				LOGGER.debug("Signature run loaded", new Exception());
				lastPrintTime = time;
			}
		}
	}
}
