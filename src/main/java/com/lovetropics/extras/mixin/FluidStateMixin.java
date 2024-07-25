package com.lovetropics.extras.mixin;

import com.lovetropics.extras.ExtendedFluidState;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FluidState.class)
public class FluidStateMixin implements ExtendedFluidState {
	@Unique
	private boolean noDripParticles;

	@Override
	public void setNoDripParticles() {
		noDripParticles = true;
	}

	@Inject(method = "getDripParticle", at = @At("HEAD"), cancellable = true)
	private void getDripParticles(CallbackInfoReturnable<ParticleOptions> ci) {
		if (noDripParticles) {
			ci.setReturnValue(null);
		}
	}
}
