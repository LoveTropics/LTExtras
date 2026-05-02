package com.lovetropics.extras.client.block;

import com.lovetropics.extras.block.RoatedDisplayBlock;
import com.lovetropics.extras.block.entity.DisplayBlockEntity;
import com.lovetropics.extras.client.block.state.DisplayBlockRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class DisplayBlockRender implements BlockEntityRenderer<DisplayBlockEntity, DisplayBlockRenderState> {

    private final ItemModelResolver itemModelResolver;

    public DisplayBlockRender(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public DisplayBlockRenderState createRenderState() {
        return new DisplayBlockRenderState();
    }

    @Override
    public void extractRenderState(DisplayBlockEntity blockEntity, DisplayBlockRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        BlockPos relativePos = blockEntity.getBlockPos();
        Direction rotationAngle = blockEntity.getBlockState().getValueOrElse(RoatedDisplayBlock.FACING, null);
        if (rotationAngle != null) {
            relativePos = relativePos.relative(rotationAngle);
        } else {
            relativePos = relativePos.above();
        }
        state.rotationAngle = rotationAngle;
        state.lightCoords = LevelRenderer.getLightCoords(LevelRenderer.BrightnessGetter.DEFAULT, blockEntity.getLevel(), blockEntity.getBlockState(), relativePos);

        itemModelResolver.updateForTopItem(state.itemStack, blockEntity.getItemStack(), ItemDisplayContext.GROUND, null, null, 0);
    }

    @Override
    public void submit(DisplayBlockRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        poseStack.pushPose();
        if (state.rotationAngle != null) {
            poseStack.translate(state.rotationAngle.getStepX() == 0 ? 0.5 : 1.5f, state.rotationAngle.getStepY() == 0 ? 0.5 : 1.5f, state.rotationAngle.getStepZ() == 0 ? 0.5 : 1.5f);
        } else {
            poseStack.translate(0.5F, 1.15F, 0.5F);
        }
        state.itemStack.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
    }
}
