package com.lovetropics.extras.mixin.client.perf;

import com.lovetropics.extras.perf.BiomeColorSampler;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.chunk.ChunkRenderCache;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.palette.PalettedContainer;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColors;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.level.ColorResolver;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.Collections;

@Mixin(ChunkRenderCache.class)
public abstract class ChunkRenderCacheMixin {
    @Shadow @Final protected World level;

    @Shadow @Final protected BlockState[] blockStates;
    @Shadow @Final protected FluidState[] fluidStates;

    @Shadow
    protected abstract int index(int x, int y, int z);

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

        this.fillBlockData(world, chunkStartX, chunkStartZ, chunks, startPos, endPos);
    }

    private void fillBlockData(World world, int minChunkX, int minChunkZ, Chunk[][] chunks, BlockPos startPos, BlockPos endPos) {
        int minChunkY = 0;
        int maxChunkX = minChunkX + chunks.length;
        int maxChunkY = world.getMaxBuildHeight() >> 4;
        int maxChunkZ = minChunkZ + chunks[0].length;

        // we can fill chunk data a lot faster than vanilla by operating on the lower level data structures directly
        BlockState[] blockStates = this.blockStates;
        FluidState[] fluidStates = this.fluidStates;
        Arrays.fill(blockStates, Blocks.AIR.defaultBlockState());
        Arrays.fill(fluidStates, Fluids.EMPTY.defaultFluidState());

        for (int chunkZ = minChunkZ; chunkZ < maxChunkZ; chunkZ++) {
            int minBlockZ = Math.max(chunkZ << 4, startPos.getZ());
            int maxBlockZ = Math.min((chunkZ << 4) + 15, endPos.getZ());

            for (int chunkX = minChunkX; chunkX < maxChunkX; chunkX++) {
                int minBlockX = Math.max(chunkX << 4, startPos.getX());
                int maxBlockX = Math.min((chunkX << 4) + 15, endPos.getX());

                Chunk chunk = chunks[chunkX - minChunkX][chunkZ - minChunkZ];
                ChunkSection[] sections = chunk.getSections();
                for (int chunkY = minChunkY; chunkY < maxChunkY; chunkY++) {
                    ChunkSection section = sections[chunkY];
                    if (section == null || section.isEmpty()) {
                        continue;
                    }

                    PalettedContainer<BlockState> blocks = section.getStates();

                    int minBlockY = Math.max(chunkY << 4, startPos.getY());
                    int maxBlockY = Math.min((chunkY << 4) + 15, endPos.getY());

                    for (int y = minBlockY; y <= maxBlockY; y++) {
                        for (int z = minBlockZ; z <= maxBlockZ; z++) {
                            for (int x = minBlockX; x <= maxBlockX; x++) {
                                BlockState block = blocks.get(x & 15, y & 15, z & 15);
                                FluidState fluid = block.getFluidState();

                                int index = this.index(x, y, z);
                                blockStates[index] = block;
                                fluidStates[index] = fluid;
                            }
                        }
                    }
                }
            }
        }
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/BlockPos;betweenClosed(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;)Ljava/lang/Iterable;"))
    private Iterable<BlockPos> initBlockAndFluidCaches(BlockPos firstPos, BlockPos secondPos) {
        return Collections.emptyList();
    }

    /**
     * @reason redirect to local optimized cache for standard biome color accessors
     * @author Gegy
     */
    // note: this will not port to 1.18 given vertical biomes! sampling the whole chunk biome volume is too expensive.
    @Overwrite
    public int getBlockTint(BlockPos pos, ColorResolver resolver) {
        if (resolver == BiomeColors.GRASS_COLOR_RESOLVER) {
            BiomeColorSampler.Buffer grassColors = this.grassColors;
            if (grassColors == null) {
                this.grassColors = grassColors = this.getBiomeColorSampler().sample(BiomeColors.GRASS_COLOR_RESOLVER);
            }
            return grassColors.get(pos);
        } else if (resolver == BiomeColors.WATER_COLOR_RESOLVER) {
            BiomeColorSampler.Buffer waterColors = this.waterColors;
            if (waterColors == null) {
                this.waterColors = waterColors = this.getBiomeColorSampler().sample(BiomeColors.WATER_COLOR_RESOLVER);
            }
            return waterColors.get(pos);
        } else if (resolver == BiomeColors.FOLIAGE_COLOR_RESOLVER) {
            BiomeColorSampler.Buffer foliageColors = this.foliageColors;
            if (foliageColors == null) {
                this.foliageColors = foliageColors = this.getBiomeColorSampler().sample(BiomeColors.FOLIAGE_COLOR_RESOLVER);
            }
            return foliageColors.get(pos);
        }

        return this.level.getBlockTint(pos, resolver);
    }

    @Unique
    private BiomeColorSampler getBiomeColorSampler() {
        BiomeColorSampler sampler = this.biomeColorSampler;
        if (sampler == null) {
            this.biomeColorSampler = sampler = BiomeColorSampler.create(this.level, this.chunkPos);
        }
        return sampler;
    }
}
