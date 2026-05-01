package com.lovetropics.extras.mixin.client;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.client.entity.state.HoniedShulkerRenderState;
import net.minecraft.client.renderer.entity.ShulkerRenderer;
import net.minecraft.client.renderer.entity.state.ShulkerRenderState;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShulkerRenderer.class)
public class ShulkerRendererMixin {
    @Unique
    private static final Identifier HONIED_TEXTURE_LOCATION = LTExtras.location("textures/entity/honied_shulker.png");

    @Inject(method = "getTextureLocation(Lnet/minecraft/client/renderer/entity/state/ShulkerRenderState;)Lnet/minecraft/resources/Identifier;", at = @At("HEAD"), cancellable = true)
    private void getTextureLocation(ShulkerRenderState state, CallbackInfoReturnable<Identifier> cir) {
        Boolean isHonied = state.getRenderData(HoniedShulkerRenderState.HONIED);
        if (Boolean.TRUE.equals(isHonied)) {
            cir.setReturnValue(HONIED_TEXTURE_LOCATION);
        }
    }
}
