package com.lovetropics.extras.client.entity;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.client.entity.model.WaterCoolerModel;
import com.lovetropics.extras.client.entity.state.WaterCoolerRenderState;
import com.lovetropics.extras.entity.WaterCoolerEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

/**
 * LTExtras
 * FIRE EMOJI, FIRE EMOJI, FIRE EMOJI
 */
public class WaterCoolerRenderer extends EntityRenderer<WaterCoolerEntity, WaterCoolerRenderState> {
    private static final Identifier TEXTURE = LTExtras.location("textures/entity/water_cooler.png");

    private final WaterCoolerModel<?> model;

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
    public void submit(WaterCoolerRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        super.submit(state, poseStack, submitNodeCollector, camera);

        poseStack.pushPose();
        poseStack.scale(-1.0f, -1.0f, 1.0f);
        poseStack.translate(0.0f, -1.28f, 0.0f);
        poseStack.mulPose(Axis.YP.rotationDegrees(state.yRot));
        model.setupAnim(state);

        submitNodeCollector.submitModel(model, state, poseStack, model.renderType(TEXTURE), state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor, null);
        poseStack.popPose();
    }

    @Override
    public WaterCoolerRenderState createRenderState() {
        return new WaterCoolerRenderState();
    }
}
