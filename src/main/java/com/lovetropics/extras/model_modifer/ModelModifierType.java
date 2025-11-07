package com.lovetropics.extras.model_modifer;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.Arrays;
import java.util.List;
import java.util.function.IntFunction;

public enum ModelModifierType implements StringRepresentable {
    DEFAULT(0, "default"),
    FABULOUS(1, "fabulous"),
    FLAIL(2, "flail"),
    HOVERING(3, "hovering"),
    SHUFFLE(4, "shuffle"),
    UPSIDEDOWN(5, "upsidedown"),
    SHRUNK(6, "shrunk"),
    ENLARGED(7, "enlarged")
    ;

    public static final StringRepresentable.EnumCodec<ModelModifierType> CODEC = StringRepresentable.fromEnum(ModelModifierType::values);

    public static final IntFunction<ModelModifierType> BY_ID = ByIdMap.continuous(i -> i.id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StreamCodec<ByteBuf, ModelModifierType> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, i -> i.id);

    public static final List<String> NAMES = Arrays.stream(values()).map(ModelModifierType::getSerializedName).toList();

    private final int id;
    private final String name;

    ModelModifierType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static ModelModifierType fromName(String name) {
        return CODEC.byName(name, DEFAULT);
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
