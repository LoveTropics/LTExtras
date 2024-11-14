package com.lovetropics.extras.mixin.rejoiner;

import com.lovetropics.extras.ExtrasConfig;
import com.lovetropics.extras.rejoiner.AutoRejoinIntent;
import com.lovetropics.extras.rejoiner.ServerAutoRejoinHandler;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.commands.StopCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StopCommand.class)
public abstract class StopCommandMixin {
	@Inject(method = "lambda$register$2", at = @At("HEAD"))
	private static void executeStop(CommandContext<CommandSourceStack> context, CallbackInfoReturnable<Integer> cir) {
		if (ExtrasConfig.CLIENT_AUTO_REJOIN.get()) {
			ServerAutoRejoinHandler.broadcastRejoinIntent(context.getSource().getServer(), AutoRejoinIntent.RESTART_SERVER);
		}
	}
}
