package com.lovetropics.extras.client.entity;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.client.entity.model.ForkliftModel;
import com.lovetropics.extras.client.entity.model.SpinningSignModel;
import com.lovetropics.extras.client.entity.state.ForkliftRenderState;
import com.lovetropics.extras.client.entity.state.SpinningSignRenderState;
import com.lovetropics.extras.entity.ForkliftEntity;
import com.lovetropics.extras.entity.SpinningSignEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;

public class SpinningSignRenderer extends EntityRenderer<SpinningSignEntity, SpinningSignRenderState> {
    private static final ResourceLocation TEXTURE = LTExtras.location("textures/entity/spinning_sign.png");

    private final SpinningSignModel model;
    private final ItemModelResolver itemModelResolver;

    public SpinningSignRenderer(EntityRendererProvider.Context context) {
        super(context);
        model = new SpinningSignModel(context.bakeLayer(SpinningSignModel.LAYER_LOCATION));
        this.itemModelResolver = context.getItemModelResolver();
    }

    @Override
    public SpinningSignRenderState createRenderState() {
        return new SpinningSignRenderState();
    }

    @Override
    public void render(SpinningSignRenderState renderState, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.scale(0.3f, 0.3f, 0.3f);
        poseStack.scale(renderState.scale, renderState.scale, renderState.scale);
        poseStack.translate(0.0F, -0.5f, 0.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(180f));

        model.setupAnim(renderState);
        poseStack.mulPose(Axis.YP.rotationDegrees(renderState.yRot));
        VertexConsumer builder = bufferSource.getBuffer(model.renderType(TEXTURE));
        model.renderToBuffer(poseStack, builder, packedLight, OverlayTexture.NO_OVERLAY);
        if(renderState.text.isPresent()) {
            Component text = renderState.text.get();
            int width = Minecraft.getInstance().font.width(text);
            float scale = 0.18f;
            float y = -0.17f;
            if (width > 22) {
                int biggerThan = width - 22;
                scale = 0.18f - Mth.abs(biggerThan * 0.004f);
                y = Mth.lerp(scale / 0.18f, 0.35f, y);
            }
            for (int i = 0; i < 4; i++) {
                poseStack.pushPose();
                poseStack.mulPose(Axis.YP.rotationDegrees(i * 90f));
                //-0.17f;
                poseStack.translate(0F, y, -5.01F);
                poseStack.scale(scale, scale, scale);
                Minecraft.getInstance().font.drawInBatch(text, -(width / 2f), 0, -1, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, LightTexture.lightCoordsWithEmission(packedLight, 2));
                poseStack.popPose();
            }
        } else if(!renderState.itemStack.isEmpty()){
            for (int i = 0; i < 4; i++) {
                poseStack.pushPose();
                poseStack.mulPose(Axis.YP.rotationDegrees(i * 90f));
                poseStack.translate(0F, 0.5F, -5.01F);
                poseStack.scale(-1.0F, -1.0F, 1.0F);
                renderState.itemStack.render(poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY);
//                Minecraft.getInstance().font.drawInBatch(text, -(width / 2f), 0, -1, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, LightTexture.lightCoordsWithEmission(packedLight, 2));
                poseStack.popPose();
            }
        }
        poseStack.popPose();
    }

    @Override
    public void extractRenderState(SpinningSignEntity entity, SpinningSignRenderState reusedState, float partialTick) {
        super.extractRenderState(entity, reusedState, partialTick);
        reusedState.scale = entity.getScale();
        reusedState.text = entity.getText();
        itemModelResolver.updateForNonLiving(reusedState.itemStack, entity.getItem(), ItemDisplayContext.FIXED, entity);
    }


}
