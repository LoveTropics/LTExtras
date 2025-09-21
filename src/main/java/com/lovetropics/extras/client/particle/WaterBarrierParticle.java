package com.lovetropics.extras.client.particle;

import com.lovetropics.extras.LTExtras;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;

@EventBusSubscriber(modid = LTExtras.MODID, value = Dist.CLIENT)
public class WaterBarrierParticle extends TextureSheetParticle {
    private static final RenderPipeline RENDER_PIPELINE = RenderPipeline.builder(RenderPipelines.PARTICLE_SNIPPET)
            .withLocation(LTExtras.location("pipeline/water_barrier_particle"))
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .withDepthWrite(true)
            .withBlend(BlendFunction.TRANSLUCENT).build();

    private static final ParticleRenderType RENDER_TYPE = new ParticleRenderType("water_barrier", RenderType.create(
            "water_barrier_particle",
            1536,
            false,
            false,
            RENDER_PIPELINE,
            RenderType.CompositeState.builder()
                    .setTextureState(new RenderStateShard.TextureStateShard(TextureAtlas.LOCATION_BLOCKS, false))
                    .setOutputState(RenderStateShard.PARTICLES_TARGET)
                    .setLightmapState(RenderStateShard.LIGHTMAP)
                    .createCompositeState(false)
    ));

    WaterBarrierParticle(ClientLevel world, double x, double y, double z, ResourceLocation sprite) {
        super(world, x, y, z);
        setSprite(Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(sprite));
        gravity = 0.0F;
        lifetime = 80;
        hasPhysics = false;
    }

    @SubscribeEvent
    public static void registerRenderPipeline(RegisterRenderPipelinesEvent event) {
        event.registerPipeline(RENDER_PIPELINE);
    }

    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        Minecraft.getInstance().particleEngine.register(ExtraParticles.WATER_BARRIER.get(), new Factory());
    }

    @Override
    public ParticleRenderType getRenderType() {
        return RENDER_TYPE;
    }

    @Override
    public float getQuadSize(float scaleFactor) {
        return 0.5F;
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new WaterBarrierParticle(world, x, y, z, ResourceLocation.withDefaultNamespace("item/barrier"));
        }
    }
}
