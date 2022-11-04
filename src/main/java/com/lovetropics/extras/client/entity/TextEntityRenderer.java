package com.lovetropics.extras.client.entity;

import com.lovetropics.extras.entity.TextEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

import java.util.List;

public class TextEntityRenderer extends EntityRenderer<TextEntity> {
	private static final Minecraft CLIENT = Minecraft.getInstance();

	public TextEntityRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void render(TextEntity entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
		poseStack.pushPose();

		Matrix4f pointAlong = entity.pointAlongMatrix();
		if (pointAlong != null) {
			poseStack.mulPoseMatrix(pointAlong);
		} else {
			poseStack.mulPose(entityRenderDispatcher.cameraOrientation());
		}

		float scale = entity.scale();
		poseStack.scale(-scale, -scale, scale);

		float alpha = entity.alpha(partialTicks);
		int color = alphaToColor(alpha) | 0xffffff;
		int backgroundColor = alphaToColor(alpha * CLIENT.options.getBackgroundOpacity(0.25F));

		Font font = entityRenderDispatcher.font;
		List<FormattedCharSequence> lines = font.split(entity.text(), Integer.MAX_VALUE);
		float lineY = -(lines.size() * font.lineHeight) / 2.0f;
		for (FormattedCharSequence line : lines) {
			font.drawInBatch(line, -font.width(line) / 2.0F, lineY, color, false, poseStack.last().pose(), bufferSource, false, backgroundColor, entity.fullbright() ? 0xf000f0 : packedLight);
			lineY += font.lineHeight;
		}

		poseStack.popPose();
	}

	private static int alphaToColor(float alpha) {
		return Mth.floor(alpha * 255.0F) << 24;
	}

	@Override
	public ResourceLocation getTextureLocation(TextEntity entity) {
		return MissingTextureAtlasSprite.getLocation();
	}
}
