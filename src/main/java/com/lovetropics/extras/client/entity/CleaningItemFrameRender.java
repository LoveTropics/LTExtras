package com.lovetropics.extras.client.entity;

import com.lovetropics.extras.entity.CleaningItemFrame;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.client.renderer.entity.state.ItemFrameRenderState;
import net.minecraft.network.chat.Component;
import org.joml.Matrix4f;

public class CleaningItemFrameRender extends ItemFrameRenderer<CleaningItemFrame> {

    public CleaningItemFrameRender(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(ItemFrameRenderState renderState, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(renderState, poseStack, bufferSource, packedLight);
        if (renderState instanceof CleaingItemFrameRenderState cleaningState) {
            renderProgressBar(cleaningState, Component.literal(cleaningState.cleanTick + " / " + cleaningState.maxCleanTick), poseStack, bufferSource, packedLight);
        }
    }

    protected void renderProgressBar(CleaingItemFrameRenderState renderState, Component displayName, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        int i = 0;
        poseStack.pushPose();
        poseStack.translate(0, 1, 0);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.scale(0.025F, -0.025F, 0.025F);
        Matrix4f matrix4f = poseStack.last().pose();

        Font font = this.getFont();
        float f = -font.width(displayName) / 2.0F;
        int j = (int) (Minecraft.getInstance().options.getBackgroundOpacity(0.25F) * 255.0F) << 24;
        font.drawInBatch(displayName, f, (float) i, -2130706433, false, matrix4f, bufferSource, Font.DisplayMode.NORMAL, j, packedLight);

        poseStack.popPose();
    }

    @Override
    public ItemFrameRenderState createRenderState() {
        return new CleaingItemFrameRenderState();
    }

    @Override
    public void extractRenderState(CleaningItemFrame itemFrame, ItemFrameRenderState renderState, float partialTick) {
        super.extractRenderState(itemFrame, renderState, partialTick);
        if (renderState instanceof CleaingItemFrameRenderState cleaningState) {
            cleaningState.cleanTick = itemFrame.getCleanTick();
            cleaningState.maxCleanTick = itemFrame.getMaxCleanTick();
        }
    }

    public static class CleaingItemFrameRenderState extends ItemFrameRenderState {
        public int cleanTick;
        public int maxCleanTick;

        public CleaingItemFrameRenderState() {
            super();
        }
    }
}
