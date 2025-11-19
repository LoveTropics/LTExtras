package com.lovetropics.extras.client.entity;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.client.entity.model.WaterCoolerModel;
import com.lovetropics.extras.client.entity.state.WaterCoolerRenderState;
import com.lovetropics.extras.entity.ForkliftEntity;
import com.lovetropics.extras.entity.WaterCoolerEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

/**
 * LTExtras
 * FIRE EMOJI, FIRE EMOJI, FIRE EMOJI
 */
public class WaterCoolerRenderer extends EntityRenderer<WaterCoolerEntity, WaterCoolerRenderState> {
    private static final ResourceLocation TEXTURE = LTExtras.location("textures/entity/water_cooler.png");

    private final WaterCoolerModel<ForkliftEntity> model;

    public WaterCoolerRenderer(EntityRendererProvider.Context context) {
        super(context);
        model = new WaterCoolerModel<>(context.bakeLayer(WaterCoolerModel.LAYER_LOCATION));
    }

    @Override
    public void extractRenderState(WaterCoolerEntity entity, WaterCoolerRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.shake1AnimationState.copyFrom(entity.shake1AnimationState);
        state.shake2AnimationState.copyFrom(entity.shake2AnimationState);
        state.shake3AnimationState.copyFrom(entity.shake3AnimationState);
        state.shakeDispenseAnimationState.copyFrom(entity.shakeDispenseAnimationState);
        state.yRot = entity.getYRot(partialTick);
        state.shakeTime = entity.getShakeTime();
    }

    @Override
    public void render(WaterCoolerRenderState state, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(state, poseStack, bufferSource, packedLight);

        poseStack.pushPose();
        poseStack.scale(-1.0f, -1.0f, 1.0f);
        poseStack.translate(0.0f, -1.28f, 0.0f);
        poseStack.mulPose(Axis.YP.rotationDegrees(state.yRot));
        model.setupAnim(state);

        VertexConsumer builder = bufferSource.getBuffer(model.renderType(TEXTURE));
        model.renderToBuffer(poseStack, builder, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }

    @Override
    public WaterCoolerRenderState createRenderState() {
        return new WaterCoolerRenderState();
    }
}
