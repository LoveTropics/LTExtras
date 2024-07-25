package com.lovetropics.extras.client.particle;

import com.lovetropics.extras.LTExtras;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@EventBusSubscriber(modid = LTExtras.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class EmittedParticle extends TextureSheetParticle {
	EmittedParticle(ClientLevel world, double x, double y, double z, SpriteSet sprites) {
		super(world, x, y, z);
		lifetime = 80;
		gravity = 0.5f;
		yd = 0.75f;
		xd = (Math.random() - Math.random()) * 0.05;
		zd = (Math.random() - Math.random()) * 0.05;
		setSize(0.5f, 0.5f);

		float f = (float)(Math.random() * (double)0.3F + (double)0.6F);
		rCol = f;
		gCol = f;
		bCol = f;

		pickSprite(sprites);
	}

	@SubscribeEvent
	public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
		Minecraft.getInstance().particleEngine.register(ExtraParticles.EMITTED_PARTICLE.get(), EmittedParticle.Factory::new);
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	public static class Factory implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet sprites;

		public Factory(SpriteSet pSprites) {
			sprites = pSprites;
		}

		@Override
		public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
			return new EmittedParticle(pLevel, pX, pY, pZ, sprites);
		}
	}
}
