package com.lovetropics.extras.model_modifer;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.IntFunction;

public enum ModelModifierType implements StringRepresentable {
    DEFAULT(0, "default"),
    FABULOUS(1, "fabulous", "Fabulous Walk"),
    FLAIL(2, "flail", "Flail Walk"),
    HOVERING(3, "hovering", "Hovering"),
    SHUFFLE(4, "shuffle", "The Shuffle"),
    UPSIDEDOWN(5, "upsidedown", "Upside Down"),
    SHRUNK(6, "shrunk", "Shrunk"),
    ENLARGED(7, "enlarged", "Enlarged"),
    RAISED_HIGH_HEELS(8, "raised_high_heels"),
    SMALL_ARMS(9, "small_arms", "Small Arms"),
    LONG_ARMS(10, "long_arms", "Long Arms"),
    STIFF_LEGS(11, "stiff_legs", "Stiff Legs"),
    HOP_WALK(12, "hop_walk", "Hop Walk")
    ;

    public static final StringRepresentable.EnumCodec<ModelModifierType> CODEC = StringRepresentable.fromEnum(ModelModifierType::values);

    public static final IntFunction<ModelModifierType> BY_ID = ByIdMap.continuous(i -> i.id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StreamCodec<ByteBuf, ModelModifierType> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, i -> i.id);

    public static final List<String> NAMES = Arrays.stream(values()).map(ModelModifierType::getSerializedName).toList();

    private final int id;
    private final String name;
    @Nullable
    private final String effectName;

    /**
     * @param id numerical ID for network encoding
     * @param name serialized name
     * @param effectName localized name is value is not null a Mob Effect will be registered
     */
    ModelModifierType(int id, String name, @Nullable String effectName) {
        this.id = id;
        this.name = name;
        this.effectName = effectName;
    }

    ModelModifierType(int id, String name) {
        this(id, name, null);
    }

    @Nullable
    public String getEffectName() {
        return effectName;
    }

    public static ModelModifierType fromName(String name) {
        return CODEC.byName(name, DEFAULT);
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
