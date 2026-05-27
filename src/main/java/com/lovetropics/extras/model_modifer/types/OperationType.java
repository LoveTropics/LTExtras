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
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public record OperationType(Operation operation, Map<String, ModelPartData> modifiers) implements ModelModifier<OperationType> {

    public static final MapCodec<OperationType> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Operation.CODEC.fieldOf("operation").forGetter(OperationType::operation),
            Codec.unboundedMap(Codec.STRING, ModelPartData.CODEC).fieldOf("parts").forGetter(OperationType::modifiers)
    ).apply(i, OperationType::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, OperationType> STREAM_CODEC = StreamCodec.composite(
            Operation.STREAM_CODEC, OperationType::operation,
            ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, ModelPartData.STREAM_CODEC), OperationType::modifiers,
            OperationType::new
    );

    @Override
    public ModelModifierType<OperationType> type() {
        return ExtraModelModifierTypes.OPERATION.get();
    }

    public static class Builder {

        private final Map<String, ModelPartData> parts = new HashMap<>();
        private final Operation operation;

        public Builder(Operation operation) {
            this.operation = operation;
        }

        public static Builder builder(Operation operation) {
            return new Builder(operation);
        }

        public Builder part(String name, Consumer<ModelPartData.Builder> consumer) {
            ModelPartData.Builder builder = new ModelPartData.Builder();
            consumer.accept(builder);
            parts.put(name, builder.build());
            return this;
        }

        public Builder parts(List<String> partNames, Consumer<ModelPartData.Builder> consumer) {
            for (String partName : partNames) {
                part(partName, consumer);
            }
            return this;
        }

        public OperationType build() {
            return new OperationType(operation, parts);
        }
    }
}

