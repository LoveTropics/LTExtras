package com.lovetropics.extras.client.entity;

import com.lovetropics.extras.client.entity.state.PartyBeamRenderState;
import com.lovetropics.extras.entity.vfx.PartyBeamEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.EndCrystalModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EndCrystalRenderer;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.CommonColors;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class PartyBeamRenderer extends EntityRenderer<PartyBeamEntity, PartyBeamRenderState> {
    private static final RenderType BEAM = RenderType.entitySmoothCutout(EnderDragonRenderer.CRYSTAL_BEAM_LOCATION);

	private static final ResourceLocation END_CRYSTAL_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/end_crystal/end_crystal.png");
	private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(END_CRYSTAL_LOCATION);
	private final EndCrystalModel model;

    public PartyBeamRenderer(EntityRendererProvider.Context context) {
        super(context);
        shadowRadius = 0.5f;
		model = new EndCrystalModel(context.bakeLayer(ModelLayers.END_CRYSTAL));
    }

	@Override
	public void render(PartyBeamRenderState state, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
		poseStack.pushPose();
		poseStack.scale(2.0f, 2.0f, 2.0f);
		poseStack.translate(0.0f, -0.5f, 0.0f);
		model.setupAnim(state);
		model.renderToBuffer(poseStack, bufferSource.getBuffer(RENDER_TYPE), packedLight, OverlayTexture.NO_OVERLAY);
		poseStack.popPose();

		Vec3 beamOffset = state.beamOffset;
		if (beamOffset != null) {
			float offsetY = EndCrystalRenderer.getY(state.ageInTicks);
            float deltaX = (float) beamOffset.x;
            float deltaY = (float) beamOffset.y;
            float deltaZ = (float) beamOffset.z;
            poseStack.translate(deltaX, deltaY, deltaZ);
            renderCrystalBeams(state.color, -deltaX, -deltaY + offsetY, -deltaZ, state.ageInTicks, poseStack, bufferSource, packedLight);
        }

		super.render(state, poseStack, bufferSource, packedLight);
    }

	// Copy of EnderDragonRenderer.renderCrystalBeams with a custom color
    public void renderCrystalBeams(int color, float deltaX, float deltaY, float deltaZ, float time, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        float lengthXz = Mth.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        float length = Mth.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
        poseStack.pushPose();
        poseStack.translate(0.0F, 2.0F, 0.0F);
        poseStack.mulPose(Axis.YP.rotation((float) (-Math.atan2(deltaZ, deltaX)) - (Mth.PI / 2F)));
        poseStack.mulPose(Axis.XP.rotation((float) (-Math.atan2(lengthXz, deltaY)) - (Mth.PI / 2F)));
        VertexConsumer consumer = bufferSource.getBuffer(BEAM);
        float startTextureOffset = -time * 0.01F;
        float endTextureOffset = length / 32.0F - time * 0.01F;
        float lastX = 0.0F;
        float lastY = 0.75F;
        float lastProgress = 0.0F;
        PoseStack.Pose pose = poseStack.last();

        for (int i = 1; i <= 8; i++) {
            float x = Mth.sin(i * Mth.TWO_PI / 8.0F) * 0.75F;
            float y = Mth.cos(i * Mth.TWO_PI / 8.0F) * 0.75F;
            float progress = i / 8.0F;
            consumer.addVertex(pose, lastX * 0.2F, lastY * 0.2F, 0.0F).setColor(CommonColors.BLACK).setUv(lastProgress, startTextureOffset).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(pose, 0.0F, -1.0F, 0.0F);
            consumer.addVertex(pose, lastX, lastY, length).setColor(color).setUv(lastProgress, endTextureOffset).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(pose, 0.0F, -1.0F, 0.0F);
            consumer.addVertex(pose, x, y, length).setColor(color).setUv(progress, endTextureOffset).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(pose, 0.0F, -1.0F, 0.0F);
            consumer.addVertex(pose, x * 0.2F, y * 0.2F, 0.0F).setColor(CommonColors.BLACK).setUv(progress, startTextureOffset).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(pose, 0.0F, -1.0F, 0.0F);
            lastX = x;
            lastY = y;
            lastProgress = progress;
        }

        poseStack.popPose();
    }

	@Override
	public PartyBeamRenderState createRenderState() {
		return new PartyBeamRenderState();
	}

	@Override
	public void extractRenderState(PartyBeamEntity entity, PartyBeamRenderState state, float partialTick) {
		super.extractRenderState(entity, state, partialTick);
		state.ageInTicks = entity.time + partialTick;
		state.showsBottom = entity.showsBottom();
		BlockPos target = entity.getBeamTarget();
		if (target != null) {
			state.beamOffset = Vec3.atCenterOf(target).subtract(entity.getPosition(partialTick));
		} else {
			state.beamOffset = null;
		}
		Vector3f color = entity.getColor();
		state.color = ARGB.colorFromFloat(1.0f, color.x, color.y, color.z);
	}

	@Override
    public boolean shouldRender(PartyBeamEntity entity, Frustum frustum, double cameraX, double cameraY, double cameraZ) {
        return super.shouldRender(entity, frustum, cameraX, cameraY, cameraZ) || entity.getBeamTarget() != null;
    }
}
