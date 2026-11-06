package com.lovetropics.extras.mixin;

import com.lovetropics.extras.duck.ForceableArgument;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.commands.arguments.selector.options.EntitySelectorOptions;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.function.Predicate;

@Mixin(EntitySelectorOptions.class)
public abstract class MixinEntitySelectorOptions {
	@Shadow
	private static void register(String id, EntitySelectorOptions.Modifier handler, Predicate<EntitySelectorParser> condition, Component description) {
		throw new IllegalStateException("Unimplemented mixin");
	}

	@SuppressWarnings("rawtypes")
	@Shadow
	@Final
	private static Map OPTIONS;

	@Inject(method = "bootStrap", at = @At("TAIL"))
	private static void injectForce(CallbackInfo info) {
		if (!OPTIONS.containsKey("force")) {
			register("force", reader -> {
				reader.setWorldLimited();
				((ForceableArgument) reader).ltextras$setForced(reader.getReader().readBoolean());
			}, reader -> !((ForceableArgument) reader).ltextras$isForced(), Component.translatable("argument.entity.options.force.description"));
		}
	}
}
