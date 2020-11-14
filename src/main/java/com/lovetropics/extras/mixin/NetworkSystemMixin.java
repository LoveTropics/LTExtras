package com.lovetropics.extras.mixin;

import net.minecraft.network.NetworkSystem;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(NetworkSystem.class)
public class NetworkSystemMixin {
	@Redirect(
			method = "tick",
			at = @At(
					value = "INVOKE",
					target = "Lorg/apache/logging/log4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V",
					remap = false
			)
	)
	private void warn(Logger logger, String message, Object p0, Object p1) {
		((Exception) p1).printStackTrace();
	}
}
