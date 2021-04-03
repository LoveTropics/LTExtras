package com.lovetropics.extras.mixin;

import com.lovetropics.extras.ExtendedFluidState;
import net.minecraft.fluid.FluidState;
import net.minecraft.particles.IParticleData;
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
		this.noDripParticles = true;
	}

	@Inject(method = "getDripParticleData", at = @At("HEAD"), cancellable = true)
	private void getDripParticles(CallbackInfoReturnable<IParticleData> ci) {
		if (this.noDripParticles) {
			ci.setReturnValue(null);
		}
	}
}
