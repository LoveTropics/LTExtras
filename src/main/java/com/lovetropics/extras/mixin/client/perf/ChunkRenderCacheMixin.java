package com.lovetropics.extras.mixin.client.perf;

import com.lovetropics.extras.perf.BiomeColorSampler;
import net.minecraft.client.renderer.chunk.ChunkRenderCache;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColors;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.level.ColorResolver;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// note: this will not port to 1.18 given vertical biomes! sampling the whole chunk biome volume is too expensive.
@Mixin(ChunkRenderCache.class)
public class ChunkRenderCacheMixin {
    @Shadow @Final protected World world;

    @Unique
    private ChunkPos chunkPos;
    @Unique
    private BiomeColorSampler biomeColorSampler;

    @Unique
    private BiomeColorSampler.Buffer grassColors;
    @Unique
    private BiomeColorSampler.Buffer waterColors;
    @Unique
    private BiomeColorSampler.Buffer foliageColors;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(World world, int chunkStartX, int chunkStartZ, Chunk[][] chunks, BlockPos startPos, BlockPos endPos, CallbackInfo ci) {
        BlockPos centerPos = new BlockPos(
                (startPos.getX() + endPos.getX()) / 2,
                (startPos.getY() + endPos.getY()) / 2,
                (startPos.getZ() + endPos.getZ()) / 2
        );
        this.chunkPos = new ChunkPos(centerPos.getX() >> 4, centerPos.getZ() >> 4);
    }

    /**
     * @reason redirect to local optimized cache for standard biome color accessors
     * @author Gegy
     */
    @Overwrite
    public int getBlockColor(BlockPos pos, ColorResolver resolver) {
        if (resolver == BiomeColors.GRASS_COLOR) {
            BiomeColorSampler.Buffer grassColors = this.grassColors;
            if (grassColors == null) {
                this.grassColors = grassColors = this.getBiomeColorSampler().sample(BiomeColors.GRASS_COLOR);
            }
            return grassColors.get(pos);
        } else if (resolver == BiomeColors.WATER_COLOR) {
            BiomeColorSampler.Buffer waterColors = this.waterColors;
            if (waterColors == null) {
                this.waterColors = waterColors = this.getBiomeColorSampler().sample(BiomeColors.WATER_COLOR);
            }
            return waterColors.get(pos);
        } else if (resolver == BiomeColors.FOLIAGE_COLOR) {
            BiomeColorSampler.Buffer foliageColors = this.foliageColors;
            if (foliageColors == null) {
                this.foliageColors = foliageColors = this.getBiomeColorSampler().sample(BiomeColors.FOLIAGE_COLOR);
            }
            return foliageColors.get(pos);
        }

        return this.world.getBlockColor(pos, resolver);
    }

    @Unique
    private BiomeColorSampler getBiomeColorSampler() {
        BiomeColorSampler sampler = this.biomeColorSampler;
        if (sampler == null) {
            this.biomeColorSampler = sampler = BiomeColorSampler.create(this.world, this.chunkPos);
        }
        return sampler;
    }
}
