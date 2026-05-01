package com.lovetropics.extras.client.entity;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.entity.ForkliftEntity;
import com.lovetropics.extras.client.entity.model.ForkliftModel;
import com.lovetropics.extras.client.entity.state.ForkliftRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.AABB;

public class ForkliftRenderer extends EntityRenderer<ForkliftEntity, ForkliftRenderState> {
    private static final Identifier TEXTURE = LTExtras.location("textures/entity/forklift.png");
    private static final RenderType LIGHTS = RenderTypes.eyes(LTExtras.location("textures/entity/forklift_lights.png"));

    private final ForkliftModel model;

    private static final boolean FORKLIFT_PICKUP_DEBUG = false;

    public ForkliftRenderer(EntityRendererProvider.Context context) {
        super(context);
        model = new ForkliftModel(context.bakeLayer(ForkliftModel.LAYER_LOCATION));
    }

    @Override
    public void extractRenderState(ForkliftEntity entity, ForkliftRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.yRot = entity.getYRot(partialTick);
        state.wheelRot = entity.getWheelRot(partialTick);
        state.forkHeight = entity.getRenderForkHeight(partialTick);
        state.pickupAABB = entity.getPickupAABB();
    }

    @Override
    public void submit(ForkliftRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        super.submit(state, poseStack, submitNodeCollector, camera);

        poseStack.pushPose();
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.scale(ForkliftEntity.FORKLIFT_SCALE, ForkliftEntity.FORKLIFT_SCALE, ForkliftEntity.FORKLIFT_SCALE);
        poseStack.translate(0.0F, EntityModel.MODEL_Y_OFFSET, 0.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(180f));
        poseStack.mulPose(Axis.YP.rotationDegrees(state.yRot));

        model.setupAnim(state);

        submitNodeCollector.submitModel(model, state, poseStack, model.renderType(TEXTURE), state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor, null);
        submitNodeCollector.submitModel(model, state, poseStack, LIGHTS, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor, null);

        poseStack.popPose();

        if (FORKLIFT_PICKUP_DEBUG) {
            poseStack.pushPose();
            // Todo 26.1 Port
            AABB move = state.pickupAABB.move(-state.x, -state.y, -state.z);
            Gizmos.cuboid(move, GizmoStyle.fill(-1));
            poseStack.popPose();
        }
    }

    @Override
    public ForkliftRenderState createRenderState() {
        return new ForkliftRenderState();
    }
}
