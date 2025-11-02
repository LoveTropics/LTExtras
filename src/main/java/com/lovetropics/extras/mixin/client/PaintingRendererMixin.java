package com.lovetropics.extras.mixin.client;

import com.lovetropics.extras.client.PaintingOverlayRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.PaintingRenderer;
import net.minecraft.client.renderer.entity.state.PaintingRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PaintingRenderer.class)
public class PaintingRendererMixin {

    @Inject(method="render(Lnet/minecraft/client/renderer/entity/state/PaintingRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("HEAD"))
    public void renderOverPainting(PaintingRenderState renderState, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CallbackInfo ci) {
        PaintingOverlayRenderer.renderOverlay(renderState, poseStack, bufferSource, packedLight);
    }
}
