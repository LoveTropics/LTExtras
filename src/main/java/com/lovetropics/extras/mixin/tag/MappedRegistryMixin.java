package com.lovetropics.extras.mixin.tag;

import com.lovetropics.extras.EverythingTag;
import net.minecraft.core.MappedRegistry;
import net.minecraft.tags.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MappedRegistry.class)
public class MappedRegistryMixin<T> {
	@Inject(method = "isKnownTagName", at = @At("HEAD"), cancellable = true)
	private void isKnownTagName(TagKey<T> key, CallbackInfoReturnable<Boolean> ci) {
		if (key.location().equals(EverythingTag.ID)) {
			ci.setReturnValue(true);
		}
	}
}
