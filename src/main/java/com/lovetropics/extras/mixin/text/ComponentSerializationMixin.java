package com.lovetropics.extras.mixin.text;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.text.ChatHighlightContents;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.util.ExtraCodecs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ComponentSerialization.class)
public class ComponentSerializationMixin {
    @Inject(method = "bootstrap", at = @At("RETURN"))
    private static void bootstrap(ExtraCodecs.LateBoundIdMapper<String, MapCodec<? extends ComponentContents>> contentTypes, CallbackInfo ci) {
        contentTypes.put(LTExtras.id("chat_highlight").toString(), ChatHighlightContents.CODEC);
    }
}
