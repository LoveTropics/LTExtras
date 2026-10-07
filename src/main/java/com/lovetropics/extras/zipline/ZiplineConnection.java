package com.lovetropics.extras.zipline;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;

public record ZiplineConnection(BlockPos target, int slack) {
    public static final Codec<ZiplineConnection> CODEC = RecordCodecBuilder.create(i -> i.group(
            BlockPos.CODEC.fieldOf("target").forGetter(ZiplineConnection::target),
            Codec.INT.optionalFieldOf("slack", 0).forGetter(ZiplineConnection::slack)
    ).apply(i, ZiplineConnection::new));

    public ZiplineConnection withSlack(int slack) {
        return new ZiplineConnection(target, slack);
    }
}
