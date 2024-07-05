package com.lovetropics.extras.client.particle;

import com.lovetropics.extras.LTExtras;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ExtraParticles {
	public static final DeferredRegister<ParticleType<?>> REGISTER = DeferredRegister.create(Registries.PARTICLE_TYPE, LTExtras.MODID);

	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> WATER_BARRIER = REGISTER.register("water_barrier", () -> new SimpleParticleType(false));
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> CHECKPOINT = REGISTER.register("checkpoint", () -> new SimpleParticleType(false));
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> EMITTED_PARTICLE = REGISTER.register("emitted_particle", () -> new SimpleParticleType(false));
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> EMITTED_FIRE_PARTICLE = REGISTER.register("emitted_fire_particle", () -> new SimpleParticleType(false));
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> EMITTED_HEARTS_PARTICLE = REGISTER.register("emitted_hearts_particle", () -> new SimpleParticleType(false));

	@OnlyIn(Dist.CLIENT)
	public static TextureAtlasSprite getItemSprite(final ClientLevel level, final ItemStack stack) {
		final BakedModel model = Minecraft.getInstance().getItemRenderer().getModel(stack, level, null, 0);
		return model.getOverrides().resolve(model, stack, level, null, 0).getParticleIcon(ModelData.EMPTY);
	}
}
