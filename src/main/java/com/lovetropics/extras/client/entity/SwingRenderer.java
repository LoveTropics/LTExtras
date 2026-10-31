package com.lovetropics.extras.client.entity;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.client.entity.model.SwingModel;
import com.lovetropics.extras.client.entity.state.SwingRenderState;
import com.lovetropics.extras.entity.SwingEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

public class SwingRenderer extends EntityRenderer<SwingEntity, SwingRenderState> {
    private static final Identifier TEXTURE = LTExtras.id("textures/entity/swing.png");

    private final SwingModel model;

    public SwingRenderer(EntityRendererProvider.Context context) {
        super(context);
        model = new SwingModel(context.bakeLayer(SwingModel.LAYER_LOCATION));
    }

    @Override
    public void extractRenderState(SwingEntity entity, SwingRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.prevSwingAngle = entity.prevClientSwingHeight;
        state.swingAngle = entity.clientSwingHeight;
    }

    @Override
    public SwingRenderState createRenderState() {
        return new SwingRenderState();
    }

    @Override
    public void submit(SwingRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        super.submit(state, poseStack, submitNodeCollector, camera);

        poseStack.pushPose();
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0F, EntityModel.MODEL_Y_OFFSET, 0.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(180f));

        model.setupAnim(state);

        submitNodeCollector.submitModel(model, state, poseStack, model.renderType(TEXTURE), state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor, null);

        poseStack.popPose();
    }
}
