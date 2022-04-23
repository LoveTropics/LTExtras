package com.lovetropics.extras.client.particle;

import com.lovetropics.extras.LTExtras;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.TextureSheetParticle;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.opengl.GL11;

@Mod.EventBusSubscriber(modid = LTExtras.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class WaterBarrierParticle extends TextureSheetParticle {
	WaterBarrierParticle(ClientLevel world, double x, double y, double z, ItemLike item) {
		super(world, x, y, z);
		this.setSprite(Minecraft.getInstance().getItemRenderer().getItemModelShaper().getParticleIcon(item));
		this.gravity = 0.0F;
		this.lifetime = 80;
		this.hasPhysics = false;
	}

	@SubscribeEvent
	public static void registerParticleFactories(ParticleFactoryRegisterEvent event) {
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

		@Override
		public void begin(BufferBuilder builder, TextureManager textureManager) {
			RenderSystem.disableBlend();
			RenderSystem.disableDepthTest();
			RenderSystem.depthMask(true);
			textureManager.bind(TextureAtlas.LOCATION_BLOCKS);
			builder.begin(GL11.GL_QUADS, DefaultVertexFormat.PARTICLE);
		}

		@Override
		public void end(Tesselator tessellator) {
			tessellator.end();
		}
	}
}
