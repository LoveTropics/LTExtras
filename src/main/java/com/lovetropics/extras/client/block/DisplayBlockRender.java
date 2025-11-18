package com.lovetropics.extras.client.block;

import com.lovetropics.extras.block.RoatedDisplayBlock;
import com.lovetropics.extras.block.entity.DisplayBlockEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class DisplayBlockRender implements BlockEntityRenderer<DisplayBlockEntity> {

    private final ItemRenderer itemRenderer;

    public DisplayBlockRender(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(DisplayBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, Vec3 cameraPos) {
        poseStack.pushPose();
        BlockPos relativePos = blockEntity.getBlockPos();
        if (blockEntity.getBlockState().hasProperty(RoatedDisplayBlock.FACING)) {
            Direction rotationAngle = blockEntity.getBlockState().getValue(RoatedDisplayBlock.FACING);
            poseStack.translate(rotationAngle.getStepX() == 0 ? 0.5 : 1.5f, rotationAngle.getStepY() == 0 ? 0.5 : 1.5f, rotationAngle.getStepZ() == 0 ? 0.5 : 1.5f);
            relativePos = relativePos.relative(rotationAngle);
        } else {
            poseStack.translate(0.5F, 1.15F, 0.5F);
            relativePos = relativePos.above();
        }
        int lightLevel = LevelRenderer.getLightColor(LevelRenderer.BrightnessGetter.DEFAULT, blockEntity.getLevel(), blockEntity.getBlockState(), relativePos);
        if (!blockEntity.getItemStack().isEmpty()) {
            itemRenderer.renderStatic(blockEntity.getItemStack(), ItemDisplayContext.GROUND, lightLevel, packedOverlay, poseStack, bufferSource, blockEntity.getLevel(), 0);
        }
        poseStack.popPose();
    }

}
