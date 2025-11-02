package com.lovetropics.extras.client.entity;

import com.lovetropics.extras.client.entity.state.FallingPropaguleRenderState;
import com.lovetropics.extras.entity.FallingPropagule;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.EmptyBlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;

public class FallingPropaguleRenderer extends EntityRenderer<FallingPropagule, FallingPropaguleRenderState> {

    public FallingPropaguleRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public FallingPropaguleRenderState createRenderState() {
        return new FallingPropaguleRenderState();
    }

    @Override
    public void extractRenderState(FallingPropagule entity, FallingPropaguleRenderState reusedState, float partialTick) {
        super.extractRenderState(entity, reusedState, partialTick);
    }

    @Override
    public void render(FallingPropaguleRenderState renderState, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(renderState, poseStack, bufferSource, packedLight);

        poseStack.pushPose();

        poseStack.mulPose(Axis.YP.rotationDegrees(renderState.ageInTicks * 20 % 360));
        poseStack.translate(-0.5f, 0f, -0.5f);

        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(Blocks.MANGROVE_PROPAGULE.defaultBlockState(), poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY, EmptyBlockAndTintGetter.INSTANCE, BlockPos.ZERO);

        poseStack.popPose();
    }
}
