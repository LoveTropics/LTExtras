package com.lovetropics.extras.mixin.tag;

import com.lovetropics.extras.EverythingTag;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagNetworkSerialization;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

@Mixin(TagNetworkSerialization.class)
public class TagNetworkSerializationMixin {
    @Inject(method = "serializeToNetwork", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static <T> void serializeToNetwork(final Registry<T> registry, final CallbackInfoReturnable<TagNetworkSerialization.NetworkPayload> cir, final Map<ResourceLocation, IntList> map) {
        // This tag is huge, and we reconstruct it client-side anyway
        map.remove(EverythingTag.ID);
    }
}
