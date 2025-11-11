package com.lovetropics.extras.client.block;

import com.lovetropics.extras.block.entity.WordBoxBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.ComponentRenderUtils;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class WordBoxBlockEntityRenderer implements BlockEntityRenderer<WordBoxBlockEntity> {

    public static final float OFFSET = 0.01f;
    public static final float Y_OFFSET = 0.5f;

    public WordBoxBlockEntityRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(WordBoxBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, Vec3 cameraPos) {
        final Component text = blockEntity.components().getOrDefault(DataComponents.CUSTOM_NAME, Component.literal("BOX")).copy().withStyle(ChatFormatting.BOLD);

        for (final Direction direction : Direction.Plane.HORIZONTAL) {
            poseStack.pushPose();
            if (direction == Direction.SOUTH) {
                poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
                poseStack.translate(direction.getStepX(), Y_OFFSET, -direction.getStepZ() - OFFSET);
            } else if (direction == Direction.NORTH) {
                poseStack.translate(1, Y_OFFSET, -OFFSET);
            } else {
                poseStack.popPose();
                continue;
            }

            int lightLevel = LevelRenderer.getLightColor(LevelRenderer.BrightnessGetter.DEFAULT, blockEntity.getLevel(), blockEntity.getBlockState(), blockEntity.getBlockPos().above());

            // Orient text
            poseStack.mulPose(Axis.ZP.rotationDegrees(180));

            // Scale text way down
            final float scale = 0.0121f;
            poseStack.scale(scale, scale, scale);

            List<FormattedCharSequence> list = ComponentRenderUtils.wrapComponents(text, (int) (1 / scale) - 10, Minecraft.getInstance().font);
            final int numLines = list.size();
            int i = 0;
            for (FormattedCharSequence c : list) {
                Minecraft.getInstance().font.drawInBatch(c, 5, (i - numLines / 2f - 0.5f) * Minecraft.getInstance().font.lineHeight + 1, 0xff161107, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, lightLevel);
                i++;
            }

            poseStack.popPose();
        }
    }
}
