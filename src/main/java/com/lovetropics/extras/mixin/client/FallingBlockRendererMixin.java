package com.lovetropics.extras.mixin.client;

import com.lovetropics.extras.ExtraBlocks;
import com.lovetropics.extras.block.entity.WordBoxBlockEntity;
import com.lovetropics.extras.client.block.WordBoxBlockEntityRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.minecraft.client.renderer.entity.state.FallingBlockRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = FallingBlockRenderer.class, priority = 100)
public class FallingBlockRendererMixin {

    @Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/FallingBlockRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V", at=@At(value = "RETURN"))
    public void onRender(FallingBlockRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera, CallbackInfo ci){
        if(state.movingBlockRenderState.blockState.is(ExtraBlocks.WORD_BOX)){
            poseStack.pushPose();
            poseStack.translate(-0.5, 0.0, -0.5);
            WordBoxBlockEntityRenderer.submitText(poseStack, submitNodeCollector, Minecraft.getInstance().font,
                    state.lightCoords,
                    state.getRenderDataOrDefault(WordBoxBlockEntityRenderer.KEY_COMPONENT, WordBoxBlockEntity.DEFAULT_TEXT)
            );
            poseStack.popPose();
        }
    }
}
