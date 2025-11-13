package com.lovetropics.extras.client.block;

import com.lovetropics.extras.ExtraBlocks;
import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.block.entity.WordBoxBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.ComponentRenderUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.state.FallingBlockRenderState;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.List;

public class WordBoxBlockEntityRenderer implements BlockEntityRenderer<WordBoxBlockEntity> {

    public static final ContextKey<Component> KEY_COMPONENT = new ContextKey<>(LTExtras.location("word_box/component"));
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

    public static void updateFallingBlockRenderState(FallingBlockEntity entity, FallingBlockRenderState renderState) {
        if(entity.getBlockState().is(ExtraBlocks.WORD_BOX)){
            try {
                if(entity.blockData != null && entity.blockData.contains("components")) {
                    Component text = DataComponentMap.CODEC.decode(NbtOps.INSTANCE, entity.blockData.get("components"))
                            .map((pair) -> {
                                return pair.getFirst().getOrDefault(DataComponents.CUSTOM_NAME, WordBoxBlockEntity.DEFAULT_TEXT);
                            }).getOrThrow();
                    renderState.setRenderData(KEY_COMPONENT, text);
                }
            } catch (Exception ignored) {}
        } else {
            renderState.setRenderData(KEY_COMPONENT, null);
        }
    }
}
