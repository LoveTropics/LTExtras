package com.lovetropics.extras.mixin.client;

import com.lovetropics.extras.ExtraBlocks;
import com.lovetropics.extras.block.entity.WordBoxBlockEntity;
import com.lovetropics.extras.client.block.WordBoxBlockEntityRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.minecraft.client.renderer.entity.state.FallingBlockRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = FallingBlockRenderer.class, priority = 100)
public class FallingBlockRendererMixin {

    @Inject(method = "render(Lnet/minecraft/client/renderer/entity/state/FallingBlockRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at=@At(value = "RETURN"))
    public void onRender(FallingBlockRenderState renderState, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CallbackInfo ci){
        if(renderState.blockState.is(ExtraBlocks.WORD_BOX)){
            poseStack.pushPose();
            poseStack.translate(-0.5, 0.0, -0.5);
            WordBoxBlockEntityRenderer.renderText(poseStack, bufferSource, Minecraft.getInstance().font,
                    packedLight, renderState.getRenderDataOrDefault(WordBoxBlockEntityRenderer.KEY_COMPONENT, WordBoxBlockEntity.DEFAULT_TEXT));
            poseStack.popPose();
        }
    }
}
