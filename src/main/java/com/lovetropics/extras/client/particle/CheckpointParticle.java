package com.lovetropics.extras.client.particle;

import com.lovetropics.extras.LTExtras;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@EventBusSubscriber(modid = LTExtras.MODID, value = Dist.CLIENT)
public class CheckpointParticle extends TextureSheetParticle {
	CheckpointParticle(ClientLevel world, double x, double y, double z, ResourceLocation spriteId) {
		super(world, x, y, z);
		setSprite(Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(spriteId));
		gravity = 0.0F;
		lifetime = 80;
		hasPhysics = false;
	}

	@SubscribeEvent
	public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
		Minecraft.getInstance().particleEngine.register(ExtraParticles.CHECKPOINT.get(), new Factory());
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.TERRAIN_SHEET;
	}

	@Override
	public float getQuadSize(float scaleFactor) {
		return 0.5F;
	}

	public static class Factory implements ParticleProvider<SimpleParticleType> {
		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			return new CheckpointParticle(world, x, y, z, ResourceLocation.withDefaultNamespace("item/structure_void"));
		}
	}
}
