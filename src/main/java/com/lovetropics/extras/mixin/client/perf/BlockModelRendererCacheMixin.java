package com.lovetropics.extras.mixin.client.perf;

import com.lovetropics.extras.perf.LossyLightCache;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net/minecraft/client/renderer/block/ModelBlockRenderer$Cache")
public class BlockModelRendererCacheMixin {
	@Shadow private boolean enabled;

	@Unique
	private final LossyLightCache.Packed fastPackedLightCache = new LossyLightCache.Packed(128);
	@Unique
	private final LossyLightCache.Brightness fastBrightnessCache = new LossyLightCache.Brightness(128);

	@Inject(method = "disable()V", at = @At("RETURN"))
	private void disable(CallbackInfo ci) {
		this.fastPackedLightCache.clear();
		this.fastBrightnessCache.clear();
	}

	/**
	 * @reason use optimised cache implementation
	 * @author Gegy
	 */
	@Overwrite
	public int getLightColor(BlockState state, BlockAndTintGetter world, BlockPos pos) {
		if (!this.enabled) {
			return LevelRenderer.getLightColor(world, state, pos);
		}

		long posKey = pos.asLong();
		LossyLightCache.Packed cache = this.fastPackedLightCache;

		int light = cache.get(posKey);
		if (light != Integer.MAX_VALUE) {
			return light;
		}

		light = LevelRenderer.getLightColor(world, state, pos);
		cache.put(posKey, light);

		return light;
	}

	/**
	 * @reason use optimised cache implementation
	 * @author Gegy
	 */
	@Overwrite
	public float getShadeBrightness(BlockState state, BlockAndTintGetter world, BlockPos pos) {
		if (!this.enabled) {
			return state.getShadeBrightness(world, pos);
		}

		long posKey = pos.asLong();
		LossyLightCache.Brightness cache = this.fastBrightnessCache;

		float brightness = cache.get(posKey);
		if (!Float.isNaN(brightness)) {
			return brightness;
		}

		brightness = state.getShadeBrightness(world, pos);
		cache.put(posKey, brightness);

		return brightness;
	}
}
