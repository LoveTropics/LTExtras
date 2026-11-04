package com.lovetropics.extras.client.entity;

import com.lovetropics.extras.ExtraTags;
import com.lovetropics.extras.client.entity.state.BlockFormedEntityRenderState;
import com.lovetropics.extras.entity.BlockFormationEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.Map;

public abstract class BlockFormedEntityRenderer<T extends BlockFormationEntity, S extends BlockFormedEntityRenderState> extends EntityRenderer<T, S> {
    private final BlockModelResolver blockModelResolver;

    public BlockFormedEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        blockModelResolver = context.getBlockModelResolver();
    }

    @Override
    public void extractRenderState(T entity, S state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        if(entity.getBlockFormation() == null) return;
        state.xRot = entity.getXRot(partialTick);
        state.yRot = entity.getYRot(partialTick);
        Map<BlockPos, StructureTemplate.StructureBlockInfo> blocks = entity.getBlockFormation().getBlocks();
        for (BlockPos blockPos : blocks.keySet()) {
            BlockFormedEntityRenderState.BlockModelRenderStateExtended blockModelRenderState =
                    state.statesToRender.computeIfAbsent(blockPos, _ -> new BlockFormedEntityRenderState.BlockModelRenderStateExtended());
            blockModelResolver.update(blockModelRenderState, blocks.get(blockPos).state(), BlockDisplayContext.create());
            if(blocks.get(blockPos).state().is(ExtraTags.Blocks.DONT_MOVE)){
                blockModelRenderState.dontMove = true;
            }
            state.statesToRender.put(blockPos, blockModelRenderState);
        }
    }

    public abstract void submitTransforms(S state, PoseStack stack);

    @Override
    public void submit(S state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        super.submit(state, poseStack, submitNodeCollector, camera);
        poseStack.pushPose();
        for (BlockPos blockPos : state.statesToRender.keySet()) {
            if(!state.statesToRender.get(blockPos).dontMove) continue;
            poseStack.pushPose();
            poseStack.translate(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            BlockModelRenderState blockModelRenderState = state.statesToRender.get(blockPos);
            blockModelRenderState.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
            poseStack.popPose();
        }
        poseStack.pushPose();
        submitTransforms(state, poseStack);
        for (BlockPos blockPos : state.statesToRender.keySet()) {
            if(state.statesToRender.get(blockPos).dontMove) continue;
            poseStack.pushPose();
            poseStack.translate(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            BlockModelRenderState blockModelRenderState = state.statesToRender.get(blockPos);
            blockModelRenderState.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
            poseStack.popPose();
        }
        poseStack.popPose();
        poseStack.popPose();
    }
}
