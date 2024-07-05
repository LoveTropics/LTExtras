package com.lovetropics.extras.effect;

import com.lovetropics.extras.LTExtras;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ExtraEffects {
	public static final DeferredRegister<MobEffect> REGISTER = DeferredRegister.create(Registries.MOB_EFFECT, LTExtras.MODID);

	public static final DeferredHolder<MobEffect, FishEyeEffect> FISH_EYE = REGISTER.register("fish_eye", () -> new FishEyeEffect(MobEffectCategory.BENEFICIAL, 0x75d7ff));
}
