package com.lovetropics.extras.client.entity;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.client.entity.model.RafflesiaModel;
import com.lovetropics.extras.client.entity.model.WaterCoolerModel;
import com.lovetropics.extras.entity.RafflesiaEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

public class RafflesiaRenderer extends EntityRenderer<RafflesiaEntity, EntityRenderState> {
    private static final Identifier TEXTURE = LTExtras.id("textures/entity/rafflesia.png");
    private final RafflesiaModel model;

    public RafflesiaRenderer(EntityRendererProvider.Context context) {
        super(context);
        model = new RafflesiaModel(context.bakeLayer(RafflesiaModel.LAYER));
    }

    @Override
    public void extractRenderState(RafflesiaEntity entity, EntityRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
    }

    @Override
    public void submit(EntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        super.submit(state, poseStack, submitNodeCollector, camera);

        poseStack.pushPose();
        poseStack.translate(0, 1.5f, 0);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180));
        model.setupAnim(state);
        submitNodeCollector.submitModel(model, state, poseStack, model.renderType(TEXTURE), state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor, null);
        poseStack.popPose();
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }
}
