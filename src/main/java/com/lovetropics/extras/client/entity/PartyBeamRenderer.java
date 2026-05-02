package com.lovetropics.extras.client.entity;

import com.lovetropics.extras.client.entity.state.PartyBeamRenderState;
import com.lovetropics.extras.entity.vfx.PartyBeamEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.crystal.EndCrystalModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EndCrystalRenderer;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3fc;

public class PartyBeamRenderer extends EntityRenderer<PartyBeamEntity, PartyBeamRenderState> {
    private static final RenderType BEAM = RenderTypes.endCrystalBeam(EnderDragonRenderer.CRYSTAL_BEAM_LOCATION);

    private static final Identifier END_CRYSTAL_LOCATION = Identifier.withDefaultNamespace("textures/entity/end_crystal/end_crystal.png");
    private static final RenderType RENDER_TYPE = RenderTypes.endCrystalBeam(END_CRYSTAL_LOCATION);
    private final EndCrystalModel model;

    public PartyBeamRenderer(EntityRendererProvider.Context context) {
        super(context);
        shadowRadius = 0.5f;
        model = new EndCrystalModel(context.bakeLayer(ModelLayers.END_CRYSTAL));
    }

    @Override
    public void submit(PartyBeamRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.scale(2.0f, 2.0f, 2.0f);
        poseStack.translate(0.0f, -0.5f, 0.0f);
        model.setupAnim(state);
        submitNodeCollector.submitModel(model, state, poseStack, RENDER_TYPE, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor, null);
        poseStack.popPose();

        Vec3 beamOffset = state.beamOffset;
        if (beamOffset != null) {
            float offsetY = EndCrystalRenderer.getY(state.ageInTicks);
            float deltaX = (float) beamOffset.x;
            float deltaY = (float) beamOffset.y;
            float deltaZ = (float) beamOffset.z;
            poseStack.translate(deltaX, deltaY, deltaZ);
            submitCrystalBeams(state.color, -deltaX, -deltaY + offsetY, -deltaZ, state.ageInTicks, poseStack, submitNodeCollector, state.lightCoords);
        }

    }

    // Copy of EnderDragonRenderer.submitCrystalBeams with a custom color
    public static void submitCrystalBeams(int color, float deltaX, float deltaY, float deltaZ, float timeInTicks, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords) {
        float horizontalLength = Mth.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        float length = Mth.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
        poseStack.pushPose();
        poseStack.translate(0.0F, 2.0F, 0.0F);
        poseStack.mulPose(Axis.YP.rotation((float)(-Math.atan2(deltaZ, deltaX)) - (float) (Math.PI / 2)));
        poseStack.mulPose(Axis.XP.rotation((float)(-Math.atan2(horizontalLength, deltaY)) - (float) (Math.PI / 2)));
        float v0 = 0.0F - timeInTicks * 0.01F;
        float v1 = length / 32.0F - timeInTicks * 0.01F;
        submitNodeCollector.submitCustomGeometry(
                poseStack,
                BEAM,
                (pose, buffer) -> {
                    int steps = 8;
                    float lastSin = 0.0F;
                    float lastCos = 0.75F;
                    float lastU = 0.0F;

                    for (int i = 1; i <= steps; i++) {
                        float sin = Mth.sin(i * (float) (Math.PI * 2) / 8.0F) * 0.75F;
                        float cos = Mth.cos(i * (float) (Math.PI * 2) / 8.0F) * 0.75F;
                        float u = i / 8.0F;
                        buffer.addVertex(pose, lastSin * 0.2F, lastCos * 0.2F, 0.0F)
                                .setColor(color)
                                .setUv(lastU, v0)
                                .setOverlay(OverlayTexture.NO_OVERLAY)
                                .setLight(lightCoords)
                                .setNormal(pose, 0.0F, -1.0F, 0.0F);
                        buffer.addVertex(pose, lastSin, lastCos, length)
                                .setColor(color)
                                .setUv(lastU, v1)
                                .setOverlay(OverlayTexture.NO_OVERLAY)
                                .setLight(lightCoords)
                                .setNormal(pose, 0.0F, -1.0F, 0.0F);
                        buffer.addVertex(pose, sin, cos, length)
                                .setColor(color)
                                .setUv(u, v1)
                                .setOverlay(OverlayTexture.NO_OVERLAY)
                                .setLight(lightCoords)
                                .setNormal(pose, 0.0F, -1.0F, 0.0F);
                        buffer.addVertex(pose, sin * 0.2F, cos * 0.2F, 0.0F)
                                .setColor(color)
                                .setUv(u, v0)
                                .setOverlay(OverlayTexture.NO_OVERLAY)
                                .setLight(lightCoords)
                                .setNormal(pose, 0.0F, -1.0F, 0.0F);
                        lastSin = sin;
                        lastCos = cos;
                        lastU = u;
                    }
                }
        );
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
        Vector3fc color = entity.getColor();
        state.color = ARGB.colorFromFloat(1.0f, color.x(), color.y(), color.z());
    }

    @Override
    public boolean shouldRender(PartyBeamEntity entity, Frustum frustum, double cameraX, double cameraY, double cameraZ) {
        return super.shouldRender(entity, frustum, cameraX, cameraY, cameraZ) || entity.getBeamTarget() != null;
    }
}
