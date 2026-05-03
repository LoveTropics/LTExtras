package com.lovetropics.extras.model_modifer.types;

import com.lovetropics.extras.model_modifer.ExtraModelModifierTypes;
import com.lovetropics.extras.model_modifer.ModelModifier;
import com.lovetropics.extras.model_modifer.ModelModifierType;
import com.lovetropics.extras.model_modifer.types.data.ModelPartData;
import com.lovetropics.extras.model_modifer.types.data.Operation;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.HashMap;
import java.util.Map;

public class OperationType implements ModelModifierType<OperationType.Modifier> {

    public record Modifier(Operation operation, Map<String, ModelPartData> modifiers) implements ModelModifier<Modifier> {

        public static final MapCodec<Modifier> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                Operation.CODEC.fieldOf("operation").forGetter(Modifier::operation),
                Codec.unboundedMap(Codec.STRING, ModelPartData.CODEC).fieldOf("parts").forGetter(Modifier::modifiers)
        ).apply(i, Modifier::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, Modifier> STREAM_CODEC = StreamCodec.composite(
                Operation.STREAM_CODEC, Modifier::operation,
                ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, ModelPartData.STREAM_CODEC), Modifier::modifiers,
                Modifier::new
        );

        @Override
        public ModelModifierType<Modifier> type() {
            return ExtraModelModifierTypes.CONSTANT.get();
        }
    }

    @Override
    public MapCodec<Modifier> codec() {
        return Modifier.CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, Modifier> streamCodec() {
        return Modifier.STREAM_CODEC;
    }
}

