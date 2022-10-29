package com.lovetropics.extras.mixin.tag;

import com.lovetropics.extras.EverythingTag;
import net.minecraft.tags.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraftforge.registries.NamespacedHolderHelper")
public class NamespacedHolderHelperMixin<T> {
	@Inject(method = "isKnownTagName", at = @At("HEAD"), cancellable = true, remap = false)
	private void isKnownTagName(TagKey<T> key, CallbackInfoReturnable<Boolean> ci) {
		if (key.location().equals(EverythingTag.ID)) {
			ci.setReturnValue(true);
		}
	}
}
