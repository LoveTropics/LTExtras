package com.lovetropics.extras.client.particle;

import com.lovetropics.extras.LTExtras;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber(modid = LTExtras.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class WaterBarrierParticle extends TextureSheetParticle {
	WaterBarrierParticle(ClientLevel world, double x, double y, double z, ItemLike item) {
		super(world, x, y, z);
		setSprite(ExtraParticles.getItemSprite(world, new ItemStack(item)));
		gravity = 0.0F;
		lifetime = 80;
		hasPhysics = false;
	}

	@SubscribeEvent
	public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
		Minecraft.getInstance().particleEngine.register(ExtraParticles.WATER_BARRIER.get(), new Factory());
	}

	@Override
	public ParticleRenderType getRenderType() {
		return RenderType.INSTANCE;
	}

	@Override
	public float getQuadSize(float scaleFactor) {
		return 0.5F;
	}

	public static class Factory implements ParticleProvider<SimpleParticleType> {
		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			return new WaterBarrierParticle(world, x, y, z, Blocks.BARRIER.asItem());
		}
	}

	public static class RenderType implements ParticleRenderType {
		public static final RenderType INSTANCE = new RenderType();

		private RenderType() {
		}

		@Nullable
		@Override
		public BufferBuilder begin(Tesselator tess, TextureManager pTextureManager) {
			RenderSystem.disableBlend();
			RenderSystem.disableDepthTest();
			RenderSystem.depthMask(true);
			RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
			return tess.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
		}
	}
}
