package com.lovetropics.extras.client.datagen;

import com.lovetropics.extras.model_modifer.ExtraModelModifierTypes;
import com.lovetropics.extras.model_modifer.ExtraModelModifiers;
import com.lovetropics.extras.model_modifer.ModelModifier;
import com.lovetropics.extras.model_modifer.types.CompositeType;
import com.lovetropics.extras.model_modifer.types.OperationType;
import com.lovetropics.extras.model_modifer.types.ScaleType;
import com.lovetropics.extras.model_modifer.types.data.ModelPartData;
import com.lovetropics.extras.model_modifer.types.data.Operation;
import com.lovetropics.extras.registry.ExtraRegistries;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jspecify.annotations.NullUnmarked;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@EventBusSubscriber(Dist.CLIENT)
public class ModelModifierDataGen {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent.Client event) {
        event.createDatapackRegistryObjects(new RegistrySetBuilder()
                .add(ExtraRegistries.MODEL_MODIFIER, ModelModifierDataGen::generateModelModifiers));
    }

    private static void generateModelModifiers(BootstrapContext<ModelModifier<?>> context) {
        context.register(ExtraModelModifiers.FABULOUS, ExtraModelModifierTypes.FABULOUS.get());
        context.register(ExtraModelModifiers.FLAIL, ExtraModelModifierTypes.FLAIL.get());
        context.register(ExtraModelModifiers.SHUFFLE, ExtraModelModifierTypes.SHUFFLE.get());
        context.register(ExtraModelModifiers.UPSIDEDOWN, ExtraModelModifierTypes.UPSIDEDOWN.get());
        context.register(ExtraModelModifiers.SHRUGGY_ARMS, ExtraModelModifierTypes.SHRUGGY_ARMS.get());
        context.register(ExtraModelModifiers.ENDER_ARMS, ExtraModelModifierTypes.ENDER_ARMS.get());
        context.register(ExtraModelModifiers.HOP_WALK, ExtraModelModifierTypes.HOP_WALK.get());

        context.register(ExtraModelModifiers.SHRUNK, ScaleBuilder.of(1.0f, 0.8f, 1.0f));
        context.register(ExtraModelModifiers.ENLARGED, ScaleBuilder.of(1.0f, 1.2f, 1.0f));

        context.register(ExtraModelModifiers.STIFF_LEGS, OperationTypeBuilder.builder(Operation.SET)
                .part(PartNames.LEFT_LEG, builder -> builder.xRot(0))
                .part(PartNames.RIGHT_LEG, builder -> builder.xRot(0))
                .build());

        context.register(ExtraModelModifiers.HOVERING, CompositeBuilder.builder()
                .add(ExtraModelModifierTypes.HOVERING.get())
                .add(ExtraModelModifierTypes.NO_SHADOW.get())
                .add(OperationTypeBuilder.builder(Operation.SET)
                        .parts(List.of(PartNames.LEFT_LEG, PartNames.RIGHT_LEG, PartNames.LEFT_ARM, PartNames.RIGHT_ARM),
                                builder -> builder.xRot(0).yRot(0))
                        .build())
                .build());

        context.register(ExtraModelModifiers.SMALL_ARMS, OperationTypeBuilder.builder(Operation.REMOVE)
                .parts(List.of(PartNames.LEFT_ARM, PartNames.RIGHT_ARM), builder -> builder.y(-3.25f))
                .build());

        context.register(ExtraModelModifiers.T_POSE, CompositeBuilder.builder()
                .add(OperationTypeBuilder.builder(Operation.SET)
                        .part(PartNames.LEFT_ARM, builder -> builder.rotation(0, 0, 270))
                        .part(PartNames.RIGHT_ARM, builder -> builder.rotation(0, 0, 90))
                        .build())
                .build());

        context.register(ExtraModelModifiers.PANCAKE, CompositeBuilder.builder()
                .add(ScaleBuilder.builder()
                        .scaleY(-0.1f)
                        .build())
                .add(ExtraModelModifierTypes.NO_SHADOW.get())
                .build());
    }


    @NullUnmarked
    private static class ScaleBuilder {

        private Float scaleX;
        private Float scaleY;
        private Float scaleZ;

        public static ScaleBuilder builder() {
            return new ScaleBuilder();
        }

        public static ScaleType.Modifier of(float x, float y, float z) {
            return new ScaleType.Modifier(Optional.of(x), Optional.of(y), Optional.of(z));
        }

        public ScaleBuilder scaleX(float scaleX) {
            this.scaleX = scaleX;
            return this;
        }

        public ScaleBuilder scaleY(float scaleY) {
            this.scaleY = scaleY;
            return this;
        }

        public ScaleBuilder scaleZ(float scaleZ) {
            this.scaleZ = scaleZ;
            return this;
        }

        public ScaleType.Modifier build() {
            return new ScaleType.Modifier(Optional.ofNullable(scaleX), Optional.ofNullable(scaleY), Optional.ofNullable(scaleZ));
        }
    }

    private static class CompositeBuilder {
        private final List<ModelModifier<?>> modifiers = new ArrayList<>();

        public static CompositeBuilder builder() {
            return new CompositeBuilder();
        }

        public CompositeBuilder add(ModelModifier<?> modifier) {
            this.modifiers.add(modifier);
            return this;
        }

        public CompositeType.Modifier build() {
            return new CompositeType.Modifier(modifiers);
        }
    }

    private static class OperationTypeBuilder {

        private final Map<String, ModelPartData> parts = new HashMap<>();
        private final Operation operation;

        public OperationTypeBuilder(Operation operation) {
            this.operation = operation;
        }

        public static OperationTypeBuilder builder(Operation operation) {
            return new OperationTypeBuilder(operation);
        }

        public OperationTypeBuilder part(String name, Consumer<ModelPartData.Builder> consumer) {
            ModelPartData.Builder builder = new ModelPartData.Builder();
            consumer.accept(builder);
            parts.put(name, builder.build());
            return this;
        }

        public CompositeBuilder parts(List<String> partNames, Consumer<ModelPartData.Builder> consumer) {
            for (String partName : partNames) {
                part(partName, consumer);
            }
            return new CompositeBuilder().add(this.build());
        }

        public OperationType.Modifier build() {
            return new OperationType.Modifier(operation, parts);
        }
    }
}
