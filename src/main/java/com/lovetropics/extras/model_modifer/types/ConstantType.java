package com.lovetropics.extras.model_modifer.types;

import com.lovetropics.extras.model_modifer.ExtraModelModifierTypes;
import com.lovetropics.extras.model_modifer.ModelModifier;
import com.lovetropics.extras.model_modifer.ModelModifierType;
import com.lovetropics.extras.model_modifer.types.data.ModelPartData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.HashMap;
import java.util.Map;

public class ConstantType implements ModelModifierType<ConstantType.Data> {

    public record Data(Map<String, ModelPartData> modifiers) implements ModelModifier<Data> {

        public static final MapCodec<Data> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                Codec.unboundedMap(Codec.STRING, ModelPartData.CODEC).fieldOf("modifiers").forGetter(Data::modifiers)
        ).apply(i, Data::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, ModelPartData.STREAM_CODEC), Data::modifiers,
                Data::new
        );

        @Override
        public Data data() {
            return this;
        }

        @Override
        public ModelModifierType<Data> type() {
            return ExtraModelModifierTypes.CONSTANT.get();
        }
    }

    @Override
    public MapCodec<Data> codec() {
        return Data.CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, Data> streamCodec() {
        return Data.STREAM_CODEC;
    }
}

