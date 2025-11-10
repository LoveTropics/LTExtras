package com.lovetropics.extras.client.block;

import com.lovetropics.extras.block.entity.WordBoxBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.ComponentCollector;
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
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.List;

public class WordBoxBlockEntityRenderer implements BlockEntityRenderer<WordBoxBlockEntity> {

    public static final float OFFSET = 0.01f;
    public static final float Y_OFFSET = 0.6f;

    public WordBoxBlockEntityRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(WordBoxBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, Vec3 cameraPos) {
        final MutableComponent text = blockEntity.components().getOrDefault(DataComponents.CUSTOM_NAME, Component.literal("BOX")).copy().withStyle(s -> s.withFont(ResourceLocation.withDefaultNamespace("uniform")));
        final Style style = text.getStyle();
        final ComponentContents contents = text.getContents();

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
//            else if (direction == Direction.EAST) {
//                poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
//                poseStack.translate(1, Y_OFFSET, -1 - OFFSET);
//            } else if (direction == Direction.WEST) {
//                poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
//                poseStack.translate(0, Y_OFFSET, -OFFSET);
//            }

            int lightLevel = LevelRenderer.getLightColor(LevelRenderer.BrightnessGetter.DEFAULT, blockEntity.getLevel(), blockEntity.getBlockState(), blockEntity.getBlockPos().above());

            // Orient text
            poseStack.mulPose(Axis.ZP.rotationDegrees(180));

            // Scale text way down
            final float scale = 0.013f;
            poseStack.scale(scale, scale, scale);

            final int length = text.getString().length();
            final int numLoops = length / 12 + 1;

            List<FormattedCharSequence> list = ComponentRenderUtils.wrapComponents(text, 12, Minecraft.getInstance().font);
            for (FormattedCharSequence c : list) {

            }

            //System.out.println(numLoops);
            for (int i = 0; i < numLoops; i++) {
                final int startIndex = i * 12;
                final int endIndex = Math.min(i * 12 + 12, length);
               //System.out.println(startIndex + " " + endIndex);
               // System.out.println(text);
                final String renderedText = text.getString().substring(startIndex, endIndex);
                ComponentCollector componentcollector = new ComponentCollector();
             //  text.copy().visit(t -> t.substring(startIndex, endIndex));

                Minecraft.getInstance().font.drawInBatch(text, 5, 3 + i * 10, Color.LIGHT_GRAY.getRGB(), false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, lightLevel);
            }

            poseStack.popPose();
        }
    }
}
