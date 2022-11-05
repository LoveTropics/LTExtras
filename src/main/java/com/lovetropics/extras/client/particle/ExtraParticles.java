package com.lovetropics.extras.client.particle;

import com.lovetropics.extras.LTExtras;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ExtraParticles {
	public static final DeferredRegister<ParticleType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, LTExtras.MODID);

	public static final RegistryObject<SimpleParticleType> WATER_BARRIER = REGISTER.register("water_barrier", () -> new SimpleParticleType(false));
	public static final RegistryObject<SimpleParticleType> CHECKPOINT = REGISTER.register("checkpoint", () -> new SimpleParticleType(false));
	public static final RegistryObject<SimpleParticleType> EMITTED_PARTICLE = REGISTER.register("emitted_particle", () -> new SimpleParticleType(false));
	public static final RegistryObject<SimpleParticleType> EMITTED_FIRE_PARTICLE = REGISTER.register("emitted_fire_particle", () -> new SimpleParticleType(false));

	@OnlyIn(Dist.CLIENT)
	public static TextureAtlasSprite getItemSprite(final ClientLevel level, final ItemStack stack) {
		final BakedModel model = Minecraft.getInstance().getItemRenderer().getModel(stack, level, null, 0);
		return model.getOverrides().resolve(model, stack, level, null, 0).getParticleIcon(EmptyModelData.INSTANCE);
	}
}
