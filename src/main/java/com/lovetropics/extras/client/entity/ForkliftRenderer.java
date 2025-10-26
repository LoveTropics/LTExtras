package com.lovetropics.extras.client.entity;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.block.entity.ForkliftEntity;
import com.lovetropics.extras.client.entity.model.ForkliftModel;
import com.lovetropics.extras.client.entity.state.ForkliftRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class ForkliftRenderer extends EntityRenderer<ForkliftEntity, ForkliftRenderState> {

    private static final ResourceLocation TEXTURE = LTExtras.location("textures/entity/forklift.png");

    private final ForkliftModel<ForkliftEntity> model;

    private static final float SCALE = 1.2f;

    public ForkliftRenderer(EntityRendererProvider.Context context) {
        super(context);
        model = new ForkliftModel<>(context.bakeLayer(ForkliftModel.LAYER_LOCATION));
    }

    @Override
    public void extractRenderState(ForkliftEntity entity, ForkliftRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.yRot = entity.getYRot(partialTick);
        state.velocity = entity.getDeltaMovement().lengthSqr();
        state.forkHeight = entity.getForkHeight();
    }

    @Override
    public void render(ForkliftRenderState state, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(state, poseStack, bufferSource, packedLight);

        poseStack.pushPose();
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.scale(SCALE, SCALE, SCALE);
        poseStack.translate(0.0F, EntityModel.MODEL_Y_OFFSET, 0.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(180f));
        poseStack.mulPose(Axis.YP.rotationDegrees(state.yRot));

        model.setupAnim(state);

        VertexConsumer builder = bufferSource.getBuffer(model.renderType(TEXTURE));
        model.renderToBuffer(poseStack, builder, packedLight, OverlayTexture.NO_OVERLAY);

        poseStack.popPose();
    }

    @Override
    public ForkliftRenderState createRenderState() {
        return new ForkliftRenderState();
    }
}
