package com.lovetropics.extras.client.entity;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.client.entity.model.SwingModel;
import com.lovetropics.extras.client.entity.state.SeeSawState;
import com.lovetropics.extras.client.entity.state.SwingRenderState;
import com.lovetropics.extras.entity.SeeSawEntity;
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
import net.minecraft.util.Mth;

public class SeeSawRenderer extends BlockFormedEntityRenderer<SeeSawEntity, SeeSawState> {
    public SeeSawRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void extractRenderState(SeeSawEntity entity, SeeSawState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.prevSeeSawAngle = entity.prevClientLevel;
        state.seeSawAngle = entity.clientLevel;
    }

    @Override
    public SeeSawState createRenderState() {
        return new SeeSawState();
    }

    @Override
    public void submit(SeeSawState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(state.partialTick, state.prevSeeSawAngle, state.seeSawAngle)));
        super.submit(state, poseStack, submitNodeCollector, camera);
        poseStack.popPose();

    }
}
