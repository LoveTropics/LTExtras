package com.lovetropics.extras.client.particle;

import com.lovetropics.extras.LTExtras;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.opengl.GL11;

@Mod.EventBusSubscriber(modid = LTExtras.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class WaterBarrierParticle extends SpriteTexturedParticle {
	WaterBarrierParticle(ClientWorld world, double x, double y, double z, IItemProvider item) {
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
	public IParticleRenderType getRenderType() {
		return RenderType.INSTANCE;
	}

	@Override
	public float getQuadSize(float scaleFactor) {
		return 0.5F;
	}

	public static class Factory implements IParticleFactory<BasicParticleType> {
		@Override
		public Particle createParticle(BasicParticleType type, ClientWorld world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			return new WaterBarrierParticle(world, x, y, z, Blocks.BARRIER.asItem());
		}
	}

	public static class RenderType implements IParticleRenderType {
		public static final RenderType INSTANCE = new RenderType();

		private RenderType() {
		}

		@Override
		public void begin(BufferBuilder builder, TextureManager textureManager) {
			RenderSystem.disableBlend();
			RenderSystem.disableDepthTest();
			RenderSystem.depthMask(true);
			textureManager.bind(AtlasTexture.LOCATION_BLOCKS);
			builder.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE);
		}

		@Override
		public void end(Tessellator tessellator) {
			tessellator.end();
		}
	}
}
