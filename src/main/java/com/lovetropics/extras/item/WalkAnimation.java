package com.lovetropics.extras.item;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.function.IntFunction;

public enum WalkAnimation implements StringRepresentable {
    DEFAULT(0, "default"),
    FABULOUS(1, "fabulous"),
    FLAIL(2, "flail"),
    HOVERING(3, "hovering"),
    SHUFFLE(4, "shuffle")
    ;

    public static final Codec<WalkAnimation> CODEC = StringRepresentable.fromEnum(WalkAnimation::values);

    public static final IntFunction<WalkAnimation> BY_ID = ByIdMap.continuous(i -> i.id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StreamCodec<ByteBuf, WalkAnimation> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, i -> i.id);

    private final int id;
    private final String name;

    WalkAnimation(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
