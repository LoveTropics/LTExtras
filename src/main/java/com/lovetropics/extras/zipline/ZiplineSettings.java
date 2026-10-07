package com.lovetropics.extras.zipline;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;

/**
 * Configured with the wrench for each rope between two poles.
 *
 * @param slack          how much the rope sags
 * @param minUpwardSpeed blocks per second that riders are pulled at when the rope climbs
 * @param canLetGo       whether riders can let go by sneaking or jumping, rather than holding on until the end of the line
 */
public record ZiplineSettings(int slack, float minUpwardSpeed, boolean canLetGo) {
    public static final int MAX_SLACK = 10;
    public static final int DEFAULT_SLACK = 2;
    public static final float LOWEST_MIN_UPWARD_SPEED = 1.0f;
    public static final float HIGHEST_MIN_UPWARD_SPEED = 20.0f;
    public static final float DEFAULT_MIN_UPWARD_SPEED = 10.0f;

    public static final ZiplineSettings DEFAULT = new ZiplineSettings(DEFAULT_SLACK, DEFAULT_MIN_UPWARD_SPEED, true);

    public static final MapCodec<ZiplineSettings> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            // Not the default slack of new ropes: ropes were saved without their slack when it was 0
            Codec.INT.optionalFieldOf("slack", 0).forGetter(ZiplineSettings::slack),
            Codec.FLOAT.optionalFieldOf("min_upward_speed", DEFAULT_MIN_UPWARD_SPEED).forGetter(ZiplineSettings::minUpwardSpeed),
            Codec.BOOL.optionalFieldOf("can_let_go", true).forGetter(ZiplineSettings::canLetGo)
    ).apply(i, ZiplineSettings::new));

    public static final StreamCodec<ByteBuf, ZiplineSettings> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ZiplineSettings::slack,
            ByteBufCodecs.FLOAT, ZiplineSettings::minUpwardSpeed,
            ByteBufCodecs.BOOL, ZiplineSettings::canLetGo,
            ZiplineSettings::new
    );

    /**
     * @return these settings brought within the range that can be configured, as they might have come from a client
     */
    public ZiplineSettings validated() {
        float speed = Float.isFinite(minUpwardSpeed) ? Mth.clamp(minUpwardSpeed, LOWEST_MIN_UPWARD_SPEED, HIGHEST_MIN_UPWARD_SPEED) : DEFAULT_MIN_UPWARD_SPEED;
        return new ZiplineSettings(Mth.clamp(slack, 0, MAX_SLACK), speed, canLetGo);
    }
}
