package com.lovetropics.extras.effect;

import com.lovetropics.extras.LTExtras;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ExtraEffects {
	public static final DeferredRegister<MobEffect> REGISTER = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, LTExtras.MODID);

	public static final RegistryObject<MobEffect> FISH_EYE = REGISTER.register("fish_eye", () -> new FishEyeEffect(MobEffectCategory.BENEFICIAL, 0x75d7ff));
}
