package com.lovetropics.extras.perf;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeColors;
import net.minecraft.world.level.ColorResolver;

import javax.annotation.Nullable;

public final class BiomeColorSampler {
    final int blendRadius;

    final Shape sampleShape;

    final Biome[] biomes;
    final Biome homogenous;

    private BiomeColorSampler(int blendRadius, Shape sampleShape, Biome[] biomes, Biome homogenous) {
        this.blendRadius = blendRadius;
        this.sampleShape = sampleShape;
        this.biomes = biomes;
        this.homogenous = homogenous;
    }

    public static BiomeColorSampler create(World world, ChunkPos chunkPos) {
        int blendRadius = Minecraft.getInstance().gameSettings.biomeBlendRadius;
        Shape sampleShape = new Shape(
                chunkPos.getXStart() - blendRadius, chunkPos.getZStart() - blendRadius,
                16 + blendRadius * 2, 16 + blendRadius * 2
        );

        Biome[] biomes = sampleShape.newBiomesArray();
        Biome homogenous = sampleBiomes(world, sampleShape, biomes);

        return new BiomeColorSampler(blendRadius, sampleShape, biomes, homogenous);
    }

    @Nullable
    private static Biome sampleBiomes(World world, Shape shape, Biome[] biomes) {
        boolean homogenous = true;
        Biome lastBiome = null;

        int minX = shape.minX;
        int minZ = shape.minZ;
        int sizeX = shape.sizeX;
        int sizeZ = shape.sizeZ;

        BlockPos.Mutable mutablePos = new BlockPos.Mutable();

        for (int z = 0; z < sizeZ; z++) {
            for (int x = 0; x < sizeX; x++) {
                mutablePos.setPos(minX + x, 0, minZ + z);

                Biome biome = world.getBiome(mutablePos);
                if (homogenous && biome != lastBiome && lastBiome != null) {
                    homogenous = false;
                }

                biomes[shape.index(x, z)] = biome;
                lastBiome = biome;
            }
        }

        return homogenous ? lastBiome : null;
    }

    public Buffer sample(ColorResolver resolver) {
        if (this.homogenous != null && !isPositionDependent(resolver)) {
            return this.sampleHomogenous(this.homogenous, resolver);
        } else {
            return this.sampleHeterogeneous(this.biomes, resolver);
        }
    }

    private static boolean isPositionDependent(ColorResolver resolver) {
        return resolver != BiomeColors.GRASS_COLOR;
    }

    private Buffer sampleHomogenous(Biome biome, ColorResolver resolver) {
        int color = resolver.getColor(biome, 0.0, 0.0);
        return Buffer.homogenous(color);
    }

    private Buffer sampleHeterogeneous(Biome[] biomes, ColorResolver resolver) {
        int[] colors = this.sampleRawColors(this.sampleShape, biomes, resolver);
        if (this.blendRadius > 0) {
            colors = this.blend(colors);
        }
        return Buffer.array(colors);
    }

    private int[] sampleRawColors(Shape shape, Biome[] biomes, ColorResolver resolver) {
        int[] colors = shape.newColorsArray();

        int minX = shape.minX;
        int minZ = shape.minZ;
        int sizeX = shape.sizeX;
        int sizeZ = shape.sizeZ;

        for (int z = 0; z < sizeZ; z++) {
            for (int x = 0; x < sizeX; x++) {
                int index = shape.index(x, z);
                colors[index] = resolver.getColor(biomes[index], x + minX, z + minZ);
            }
        }

        return colors;
    }

    private int[] blend(int[] colors) {
        int blendRadius = this.blendRadius;

        Shape shape = this.sampleShape;
        Shape shapeX = shape.growAlong(Direction.Axis.X, -blendRadius);
        Shape shapeZ = shape.growAlong(Direction.Axis.Z, -blendRadius);

        colors = blendAxis(shape, shapeX, blendRadius, colors, Direction.Axis.X);
        colors = blendAxis(shapeX, shapeZ, blendRadius, colors, Direction.Axis.Z);

        return colors;
    }

    private static int[] blendAxis(Shape srcShape, Shape dstShape, int radius, int[] src, Direction.Axis axis) {
        int[] dst = dstShape.newColorsArray();
        int dstSizeX = dstShape.sizeX;
        int dstSizeZ = dstShape.sizeZ;

        int indexOffset = srcShape.indexOffsetAlong(axis);
        int dstToSrcOffset = indexOffset * radius;

        for (int dstZ = 0; dstZ < dstSizeZ; dstZ++) {
            for (int dstX = 0; dstX < dstSizeX; dstX++) {
                int srcIndex = srcShape.index(dstX, dstZ) + dstToSrcOffset;
                dst[dstShape.index(dstX, dstZ)] = blend(src, radius, srcIndex, indexOffset);
            }
        }

        return dst;
    }

    private static int blend(int[] colors, int radius, int centerIndex, int indexOffset) {
        int red = 0;
        int green = 0;
        int blue = 0;

        for (int i = -radius; i <= radius; i++) {
            int color = colors[centerIndex + indexOffset * i];
            red += (color >> 16) & 0xFF;
            green += (color >> 8) & 0xFF;
            blue += color & 0xFF;
        }

        int samples = radius * 2 + 1;
        return (red / samples & 0xFF) << 16 | (green / samples & 0xFF) << 8 | (blue / samples & 0xFF);
    }

    public interface Buffer {
        static Buffer homogenous(int color) {
            return pos -> color;
        }

        static Buffer array(int[] colors) {
            return pos -> colors[(pos.getZ() & 15) << 4 | (pos.getX() & 15)];
        }

        int get(BlockPos pos);
    }

    static final class Shape {
        final int minX;
        final int minZ;
        final int sizeX;
        final int sizeZ;

        Shape(int minX, int minZ, int sizeX, int sizeZ) {
            this.minX = minX;
            this.minZ = minZ;
            this.sizeX = sizeX;
            this.sizeZ = sizeZ;
        }

        int index(int x, int z) {
            return (z * this.sizeX) + x;
        }

        int indexOffsetAlong(Direction.Axis axis) {
            switch (axis) {
                case X: return this.index(1, 0);
                case Z: return this.index(0, 1);
                default: throw new UnsupportedOperationException();
            }
        }

        Biome[] newBiomesArray() {
            return new Biome[this.sizeX * this.sizeZ];
        }

        int[] newColorsArray() {
            return new int[this.sizeX * this.sizeZ];
        }

        Shape growAlong(Direction.Axis axis, int radius) {
            if (radius == 0) return this;
            switch (axis) {
                case X: return new Shape(this.minX - radius, this.minZ, this.sizeX + radius * 2, this.sizeZ);
                case Z: return new Shape(this.minX, this.minZ - radius, this.sizeX, this.sizeZ + radius * 2);
                default: throw new UnsupportedOperationException();
            }
        }
    }
}
