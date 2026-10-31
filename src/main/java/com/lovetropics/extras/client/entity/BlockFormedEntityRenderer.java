package com.lovetropics.extras.client.entity;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.client.entity.state.BlockFormedEntityRenderState;
import com.lovetropics.extras.client.entity.state.SeeSawState;
import com.lovetropics.extras.entity.BlockFormationEntity;
import com.lovetropics.extras.entity.SeeSawEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.block.model.BlockStateModelWrapper;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
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
        Map<BlockPos, StructureTemplate.StructureBlockInfo> blocks = entity.getBlockFormation().getBlocks();
        for (BlockPos blockPos : blocks.keySet()) {
            BlockModelRenderState blockModelRenderState =
                    state.statesToRender.computeIfAbsent(blockPos, _ -> new BlockModelRenderState());
            blockModelResolver.update(blockModelRenderState, blocks.get(blockPos).state(), BlockDisplayContext.create());
            state.statesToRender.put(blockPos, blockModelRenderState);
        }
    }

    @Override
    public void submit(S state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        super.submit(state, poseStack, submitNodeCollector, camera);

        poseStack.pushPose();
        for (BlockPos blockPos : state.statesToRender.keySet()) {
            poseStack.pushPose();
            poseStack.translate(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            BlockModelRenderState blockModelRenderState = state.statesToRender.get(blockPos);
            blockModelRenderState.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
            poseStack.popPose();
        }
        poseStack.popPose();
    }
}
