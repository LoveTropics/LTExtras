package com.lovetropics.extras.mixin.client.perf;

import com.lovetropics.extras.perf.LossyLightCache;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net/minecraft/client/renderer/BlockModelRenderer$Cache")
public class BlockModelRendererCacheMixin {
    @Shadow private boolean enabled;

    private final LossyLightCache.Packed packedLightCache = new LossyLightCache.Packed(128);
    private final LossyLightCache.Brightness brightnessCache = new LossyLightCache.Brightness(128);

    @Inject(method = "disable", at = @At("RETURN"))
    private void disable(CallbackInfo ci) {
        this.packedLightCache.clear();
        this.brightnessCache.clear();
    }

    /**
     * @reason use optimised cache implementation
     * @author Gegy
     */
    @Overwrite
    public int getPackedLight(BlockState state, IBlockDisplayReader world, BlockPos pos) {
        if (!this.enabled) {
            return WorldRenderer.getPackedLightmapCoords(world, state, pos);
        }

        long posKey = pos.toLong();
        LossyLightCache.Packed cache = this.packedLightCache;

        int light = cache.get(posKey);
        if (light != Integer.MAX_VALUE) {
            return light;
        }

        light = WorldRenderer.getPackedLightmapCoords(world, state, pos);
        cache.put(posKey, light);

        return light;
    }

    /**
     * @reason use optimised cache implementation
     * @author Gegy
     */
    @Overwrite
    public float getBrightness(BlockState state, IBlockDisplayReader world, BlockPos pos) {
        if (!this.enabled) {
            return state.getAmbientOcclusionLightValue(world, pos);
        }

        long posKey = pos.toLong();
        LossyLightCache.Brightness cache = this.brightnessCache;

        float brightness = cache.get(posKey);
        if (!Float.isNaN(brightness)) {
            return brightness;
        }

        brightness = state.getAmbientOcclusionLightValue(world, pos);
        cache.put(posKey, brightness);

        return brightness;
    }
}
