package com.lovetropics.extras.client.block;

import com.lovetropics.extras.block.entity.WordBoxBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.ComponentRenderUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.List;

public class WordBoxBlockEntityRenderer implements BlockEntityRenderer<WordBoxBlockEntity> {
    public static final float OFFSET = 0.01f;
    public static final float Y_OFFSET = 0.5f;

    private static final int PADDING = 5;
    private static final int TEXT_COLOR = 0xff161107;

    private static final float TEXT_SCALE = 0.0121f;

    private final Font font;

    public WordBoxBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        font = context.getFont();
    }

    @Override
    public void render(WordBoxBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, Vec3 cameraPos) {
        renderText(poseStack, bufferSource, font, packedLight, blockEntity.components().getOrDefault(DataComponents.CUSTOM_NAME, WordBoxBlockEntity.DEFAULT_TEXT));
    }

    public static void renderText(PoseStack poseStack, MultiBufferSource bufferSource, Font font, int packedLight, Component unstyledText) {
        Component text = unstyledText.copy().withStyle(ChatFormatting.BOLD);

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0f));
        poseStack.translate(0.0f, Y_OFFSET, -1.0f - OFFSET);
        renderFaceText(poseStack, bufferSource, font, packedLight, text);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(1.0f, Y_OFFSET, -OFFSET);
        renderFaceText(poseStack, bufferSource, font, packedLight, text);
        poseStack.popPose();
    }

    private static void renderFaceText(PoseStack poseStack, MultiBufferSource bufferSource, Font font, int packedLight, Component text) {
        poseStack.scale(-TEXT_SCALE, -TEXT_SCALE, TEXT_SCALE);

        Matrix4f pose = poseStack.last().pose();
        List<FormattedCharSequence> lines = ComponentRenderUtils.wrapComponents(text, Mth.floor(1.0f / TEXT_SCALE) - PADDING * 2, font);

        int i = 0;
        for (FormattedCharSequence line : lines) {
            float lineY = (i - lines.size() / 2.0f - 0.5f) * font.lineHeight + 1;
            font.drawInBatch(line, PADDING, lineY, TEXT_COLOR, false, pose, bufferSource, Font.DisplayMode.NORMAL, 0, packedLight);
            i++;
        }
    }
}
