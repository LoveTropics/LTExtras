package com.lovetropics.extras.client.entity;

import com.lovetropics.extras.client.entity.state.FallingPropaguleRenderState;
import com.lovetropics.extras.entity.FallingPropagule;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class FallingPropaguleRenderer extends EntityRenderer<FallingPropagule, FallingPropaguleRenderState> {

    public FallingPropaguleRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public FallingPropaguleRenderState createRenderState() {
        return new FallingPropaguleRenderState();
    }

    @Override
    public void extractRenderState(FallingPropagule entity, FallingPropaguleRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        BlockPos pos = BlockPos.containing(entity.getX(), entity.getBoundingBox().maxY, entity.getZ());
        state.movingBlockRenderState.randomSeedPos = entity.blockPosition();
        state.movingBlockRenderState.blockPos = pos;
        state.movingBlockRenderState.blockState = Blocks.MANGROVE_PROPAGULE.defaultBlockState();
        if (entity.level() instanceof ClientLevel clientLevel) {
            state.movingBlockRenderState.biome = clientLevel.getBiome(pos);
            state.movingBlockRenderState.cardinalLighting = clientLevel.cardinalLighting();
            state.movingBlockRenderState.lightEngine = clientLevel.getLightEngine();
        }
    }

    @Override
    public void submit(FallingPropaguleRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        super.submit(state, poseStack, submitNodeCollector, camera);

        poseStack.pushPose();

        poseStack.mulPose(Axis.YP.rotationDegrees(state.ageInTicks * 20 % 360));
        poseStack.translate(-0.5f, 0f, -0.5f);

        submitNodeCollector.submitMovingBlock(poseStack, state.movingBlockRenderState);
        poseStack.popPose();
    }


}
