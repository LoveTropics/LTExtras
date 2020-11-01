package com.lovetropics.extras.client.renderer;

import com.lovetropics.extras.entity.DummyPlayerEntity;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HeadLayer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.util.ResourceLocation;

public class DummyPlayerEntityRenderer extends LivingRenderer<DummyPlayerEntity, DummyPlayerModel> {

	public DummyPlayerEntityRenderer(EntityRendererManager rendererManager) {
		super(rendererManager, new DummyPlayerModel(0, false), 0.5f);
		this.addLayer(new BipedArmorLayer<>(this, new BipedModel<>(0.5F), new BipedModel<>(1.0F)));
		this.addLayer(new HeldItemLayer<>(this));
		this.addLayer(new ElytraLayer<>(this));
		this.addLayer(new HeadLayer<>(this));
	}

	@Override
	protected boolean canRenderName(DummyPlayerEntity entity) {
		return entity.getProfessionName().getUnformattedComponentText().length() > 0;
	}

	@Override
	protected void preRenderCallback(DummyPlayerEntity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
		float f = 0.9375F;
		matrixStackIn.scale(f, f, f);
	}

	@Override
	public ResourceLocation getEntityTexture(DummyPlayerEntity entity) {
		return entity.getSkin();
	}
}
