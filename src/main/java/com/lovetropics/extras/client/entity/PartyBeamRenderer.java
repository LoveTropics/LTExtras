package com.lovetropics.extras.client.entity;

import com.lovetropics.extras.entity.vfx.PartyBeamEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CommonColors;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class PartyBeamRenderer extends EntityRenderer<PartyBeamEntity> {
    private static final ResourceLocation END_CRYSTAL_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/end_crystal/end_crystal.png");
    private static final RenderType BEAM = RenderType.entitySmoothCutout(EnderDragonRenderer.CRYSTAL_BEAM_LOCATION);
    private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(END_CRYSTAL_LOCATION);
    private static final float SIN_45 = Mth.sin(Mth.PI / 4.0f);
    private final ModelPart cube;
    private final ModelPart glass;
    private final ModelPart base;

    public PartyBeamRenderer(final EntityRendererProvider.Context context) {
        super(context);
        shadowRadius = 0.5F;
        final ModelPart root = context.bakeLayer(ModelLayers.END_CRYSTAL);
        glass = root.getChild("glass");
        cube = root.getChild("cube");
        base = root.getChild("base");
    }

    @Override
    public void render(final PartyBeamEntity entity, final float yaw, final float partialTicks, final PoseStack poseStack, final MultiBufferSource bufferSource, final int packedLight) {
        poseStack.pushPose();
        final float offsetY = getY(entity, partialTicks);
        final float time = (entity.time + partialTicks) * 3.0F;
        final VertexConsumer consumer = bufferSource.getBuffer(RENDER_TYPE);
        poseStack.pushPose();
        poseStack.scale(2.0F, 2.0F, 2.0F);
        poseStack.translate(0.0F, -0.5F, 0.0F);
        if (entity.showsBottom()) {
            base.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        }

        poseStack.mulPose(Axis.YP.rotationDegrees(time));
        poseStack.translate(0.0F, 1.5F + offsetY / 2.0F, 0.0F);
        poseStack.mulPose(new Quaternionf().rotateAxis(60.0F * Mth.DEG_TO_RAD, new Vector3f(SIN_45, 0.0F, SIN_45)));
        glass.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.scale(0.875F, 0.875F, 0.875F);
        poseStack.mulPose(new Quaternionf().rotateAxis(60.0F * Mth.DEG_TO_RAD, new Vector3f(SIN_45, 0.0F, SIN_45)));
        poseStack.mulPose(Axis.YP.rotationDegrees(time));
        glass.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.scale(0.875F, 0.875F, 0.875F);
        poseStack.mulPose(new Quaternionf().rotateAxis(60.0F * Mth.DEG_TO_RAD, new Vector3f(SIN_45, 0.0F, SIN_45)));
        poseStack.mulPose(Axis.YP.rotationDegrees(time));
        cube.render(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
        poseStack.popPose();

        final BlockPos target = entity.getBeamTarget();
        if (target != null) {
            final float targetX = target.getX() + 0.5F;
            final float targetY = target.getY() + 0.5F;
            final float targetZ = target.getZ() + 0.5F;
            final float deltaX = (float) (targetX - entity.getX());
            final float deltaY = (float) (targetY - entity.getY());
            final float deltaZ = (float) (targetZ - entity.getZ());
            poseStack.translate(deltaX, deltaY, deltaZ);
            renderCrystalBeams(entity, -deltaX, -deltaY + offsetY, -deltaZ, partialTicks, entity.time, poseStack, bufferSource, packedLight);
        }

        super.render(entity, yaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    // Needed to set the color
    public void renderCrystalBeams(final PartyBeamEntity entity, final float deltaX, final float deltaY, final float deltaZ, final float partialTicks, final int time, final PoseStack poseStack, final MultiBufferSource bufferSource, final int packedLight) {
        final float lengthXz = Mth.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        final float length = Mth.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
        poseStack.pushPose();
        poseStack.translate(0.0F, 2.0F, 0.0F);
        poseStack.mulPose(Axis.YP.rotation((float) (-Math.atan2(deltaZ, deltaX)) - (Mth.PI / 2F)));
        poseStack.mulPose(Axis.XP.rotation((float) (-Math.atan2(lengthXz, deltaY)) - (Mth.PI / 2F)));
        final VertexConsumer consumer = bufferSource.getBuffer(BEAM);
        final float startTextureOffset = -(time + partialTicks) * 0.01F;
        final float endTextureOffset = length / 32.0F - (time + partialTicks) * 0.01F;
        float lastX = 0.0F;
        float lastY = 0.75F;
        float lastProgress = 0.0F;
        final PoseStack.Pose pose = poseStack.last();

        for (int i = 1; i <= 8; i++) {
            final float x = Mth.sin(i * Mth.TWO_PI / 8.0F) * 0.75F;
            final float y = Mth.cos(i * Mth.TWO_PI / 8.0F) * 0.75F;
            final float progress = i / 8.0F;
            final Vector3f color = entity.getColor();
            final int packedColor = FastColor.ARGB32.colorFromFloat(1.0f, color.x, color.y, color.z);
            consumer.addVertex(pose, lastX * 0.2F, lastY * 0.2F, 0.0F).setColor(CommonColors.BLACK).setUv(lastProgress, startTextureOffset).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(pose, 0.0F, -1.0F, 0.0F);
            consumer.addVertex(pose, lastX, lastY, length).setColor(packedColor).setUv(lastProgress, endTextureOffset).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(pose, 0.0F, -1.0F, 0.0F);
            consumer.addVertex(pose, x, y, length).setColor(packedColor).setUv(progress, endTextureOffset).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(pose, 0.0F, -1.0F, 0.0F);
            consumer.addVertex(pose, x * 0.2F, y * 0.2F, 0.0F).setColor(CommonColors.BLACK).setUv(progress, startTextureOffset).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(pose, 0.0F, -1.0F, 0.0F);
            lastX = x;
            lastY = y;
            lastProgress = progress;
        }

        poseStack.popPose();
    }

    private static float getY(final PartyBeamEntity entity, final float partialTicks) {
        final float time = entity.time + partialTicks;
        float y = Mth.sin(time * 0.2F) / 2.0F + 0.5F;
        y = (y * y + y) * 0.4F;
        return y - 1.4F;
    }

    @Override
    public ResourceLocation getTextureLocation(final PartyBeamEntity entity) {
        return END_CRYSTAL_LOCATION;
    }

    @Override
    public boolean shouldRender(final PartyBeamEntity entity, final Frustum frustum, final double cameraX, final double cameraY, final double cameraZ) {
        return super.shouldRender(entity, frustum, cameraX, cameraY, cameraZ) || entity.getBeamTarget() != null;
    }
}
