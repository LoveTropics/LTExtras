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
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;

public class ForkliftRenderer extends EntityRenderer<ForkliftEntity, ForkliftRenderState> {

    private static final ResourceLocation TEXTURE = LTExtras.location("textures/entity/forklift.png");

    private final ForkliftModel<ForkliftEntity> model;

    private static final boolean FORKLIFT_PICKUP_DEBUG = false;

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
        state.pickupAABB = entity.getPickupAABB();
    }

    @Override
    public void render(ForkliftRenderState state, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(state, poseStack, bufferSource, packedLight);

        poseStack.pushPose();
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.scale(ForkliftEntity.FORKLIFT_SCALE, ForkliftEntity.FORKLIFT_SCALE, ForkliftEntity.FORKLIFT_SCALE);
        poseStack.translate(0.0F, EntityModel.MODEL_Y_OFFSET, 0.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(180f));
        poseStack.mulPose(Axis.YP.rotationDegrees(state.yRot));

        model.setupAnim(state);

        VertexConsumer builder = bufferSource.getBuffer(model.renderType(TEXTURE));
        model.renderToBuffer(poseStack, builder, packedLight, OverlayTexture.NO_OVERLAY);

        poseStack.popPose();

        if (FORKLIFT_PICKUP_DEBUG) {
            poseStack.pushPose();
            renderHitbox(poseStack, bufferSource.getBuffer(RenderType.LINES), state.pickupAABB.move(-state.x, -state.y, -state.z));
            poseStack.popPose();
        }
    }

    private static void renderHitbox(PoseStack posStack, VertexConsumer consumer, AABB hitbox) {
        ShapeRenderer.renderLineBox(posStack, consumer, hitbox.minX, hitbox.minY, hitbox.minZ, hitbox.maxX, hitbox.maxY, hitbox.maxZ, 0, 1, 1, 1.0F);
    }

    @Override
    public ForkliftRenderState createRenderState() {
        return new ForkliftRenderState();
    }
}
