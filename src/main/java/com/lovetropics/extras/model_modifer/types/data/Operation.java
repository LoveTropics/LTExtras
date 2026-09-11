package com.lovetropics.extras.model_modifer.types.data;

import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.function.BiFunction;

public enum Operation implements StringRepresentable {
    SET("set", (_, passed) -> passed),
    ADD("add", Float::sum),
    REMOVE("remove", (original, passed) -> original - passed),
    ;

    private final String name;
    private final BiFunction<Float, Float, Float> function;

    public static final Codec<Operation> CODEC = StringRepresentable.fromEnum(Operation::values);
    public static final StreamCodec<FriendlyByteBuf, Operation> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(Operation.class);

    Operation(String name, OperationFunction function) {
        this.name = name;
        this.function = function;
    }

    public float apply(float original, float passed) {
        return function.apply(original, passed);
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    @FunctionalInterface
    interface OperationFunction extends BiFunction<Float, Float, Float> {

        @Override
        Float apply(Float original, Float passed);
    }
}
