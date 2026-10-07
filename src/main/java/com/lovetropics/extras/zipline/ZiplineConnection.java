package com.lovetropics.extras.zipline;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

/**
 * @param targetAnchorOffset where the rope is tied on the target pole, relative to it, so that the rope can be placed
 *                           without the target pole being loaded
 */
public record ZiplineConnection(BlockPos target, Vec3 targetAnchorOffset, ZiplineSettings settings) {
    public static final Codec<ZiplineConnection> CODEC = RecordCodecBuilder.create(i -> i.group(
            BlockPos.CODEC.fieldOf("target").forGetter(ZiplineConnection::target),
            Vec3.CODEC.optionalFieldOf("target_anchor", ZiplinePoleBlock.FLOOR_ANCHOR).forGetter(ZiplineConnection::targetAnchorOffset),
            ZiplineSettings.MAP_CODEC.forGetter(ZiplineConnection::settings)
    ).apply(i, ZiplineConnection::new));

    public Vec3 targetAnchor() {
        return Vec3.atLowerCornerOf(target).add(targetAnchorOffset);
    }

    public ZiplineConnection withSettings(ZiplineSettings settings) {
        return new ZiplineConnection(target, targetAnchorOffset, settings);
    }
}
