package com.lovetropics.extras.model_modifer.types;

import com.lovetropics.extras.model_modifer.ExtraModelModifierTypes;
import com.lovetropics.extras.model_modifer.ModelModifier;
import com.lovetropics.extras.model_modifer.ModelModifierType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.NullUnmarked;

import java.util.Optional;
import java.util.function.Function;

public record ScaleType(float x, float y, float z) implements ModelModifier<ScaleType>  {

    public static final MapCodec<ScaleType> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ExtraCodecs.VECTOR3F.fieldOf("scale").forGetter(ScaleType::toVector)
    ).apply(instance, ScaleType::new));

    public static StreamCodec<RegistryFriendlyByteBuf, ScaleType> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, ScaleType::x,
            ByteBufCodecs.FLOAT, ScaleType::y,
            ByteBufCodecs.FLOAT, ScaleType::z,
            ScaleType::new);

    private ScaleType(Vector3fc vec) {
        this(vec.x(), vec.y(), vec.z());
    }

    private Vector3fc toVector() {
        return new Vector3f(x, y, z);
    }

    @Override
    public ModelModifierType<ScaleType> type() {
        return ExtraModelModifierTypes.SCALE.get();
    }
}
