package com.lovetropics.extras.mixin.perf;

import net.minecraft.world.biome.FuzzedBiomeMagnifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(FuzzedBiomeMagnifier.class)
public class FuzzedBiomeMagnifierMixin {
    /**
     * @reason remove usage of floorMod
     * @author Gegy
     */
    @Overwrite
    private static double randomDouble(long seed) {
        double value = (double) (int) (seed >> 24 & 1023) / 1024.0;
        return (value - 0.5) * 0.9;
    }
}
