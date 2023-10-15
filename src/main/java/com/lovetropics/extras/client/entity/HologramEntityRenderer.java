package com.lovetropics.extras.client.entity;

import com.lovetropics.extras.ExtraUtils;
import com.lovetropics.extras.entity.HologramEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;

import java.util.List;
import java.util.function.Function;

public class HologramEntityRenderer extends EntityRenderer<HologramEntity> {
    private final Font font;
    private final EntityRenderDispatcher entityRenderDispatcher;
    private final Function<Component, List<HologramEntity.Line>> textSplitter;

    public HologramEntityRenderer(final EntityRendererProvider.Context context) {
        super(context);
        shadowRadius = 0.0f;
        entityRenderDispatcher = context.getEntityRenderDispatcher();
        font = context.getFont();
        textSplitter = component -> font.split(component, Integer.MAX_VALUE).stream()
                .map(text -> new HologramEntity.Line(text, font.width(text)))
                .toList();
    }

    @Override
    public void render(final HologramEntity entity, final float yaw, final float partialTicks, final PoseStack poseStack, final MultiBufferSource bufferSource, final int packedLight) {
        final HologramEntity.DisplayInfo display = entity.display(textSplitter);

        poseStack.pushPose();
        final Quaternionf rotation = display.rotation();
        if (rotation != null) {
            poseStack.mulPose(rotation);
        } else {
            poseStack.mulPose(ExtraUtils.rotationAboutY(entityRenderDispatcher.cameraOrientation(), new Quaternionf()));
        }

        final float scale = entity.scale();
        poseStack.scale(-scale, -scale, scale);

        final float backgroundOpacity = Minecraft.getInstance().options.getBackgroundOpacity(0.25f);
        final int backgroundColor = Mth.floor(backgroundOpacity * 255.0F) << 24;
        final int textLight = entity.fullbright() ? 0xf000f0 : packedLight;

        final int lineHeight = font.lineHeight + 1;
        float lineY = -(display.lines().size() * lineHeight) / 2.0f;
        for (final HologramEntity.Line line : display.lines()) {
            font.drawInBatch(line.text(), -line.width() / 2.0f, lineY, 0xffffffff, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, backgroundColor, textLight);
            lineY += lineHeight;
        }

        poseStack.popPose();

        super.render(entity, yaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(final HologramEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
