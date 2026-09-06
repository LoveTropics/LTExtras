package com.lovetropics.extras.environmentattribute;

import com.lovetropics.extras.duck.HasDynamicEas;
import com.lovetropics.extras.network.message.ClientboundEnvironmentAttributesPacket;
import it.unimi.dsi.fastutil.objects.Object2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import net.minecraft.world.attribute.LerpFunction;
import net.minecraft.world.attribute.modifier.AttributeModifier;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LevelDynamicEasManager implements AutoCloseable {
    private final HasDynamicEas.DecoratorHandle decoratorHandle;

    private final SortedMap<Identifier, Layer> layers = new Object2ObjectAVLTreeMap<>();
    // Each attribute we register comes with some performance cost for evaluating it, so try to keep as few registered as possible
    private final Set<EnvironmentAttribute<?>> modifiedAttributes = new ReferenceOpenHashSet<>();

    public LevelDynamicEasManager(Level level) {
        decoratorHandle = ((HasDynamicEas) level).ltextras$addEasDecorator(this::addLayersTo);
    }

    public boolean tick() {
        layers.values().removeIf(Layer::tick);
        return layers.isEmpty();
    }

    private void ensureAttributeRegistered(EnvironmentAttribute<?> attribute) {
        if (modifiedAttributes.add(attribute)) {
            decoratorHandle.rebuild();
        }
    }

    public <Value, Argument> void modifyAttribute(Identifier layerId, EnvironmentAttribute<Value> attribute, AttributeModifier<Value, Argument> modifier, Argument argument, int transitionTicks) {
        ensureAttributeRegistered(attribute);
        Layer layer = layers.computeIfAbsent(layerId, _ -> new Layer());
        layer.modifyAttribute(attribute, modifier, argument, transitionTicks);
    }

    public boolean clearAttribute(Identifier layerId, EnvironmentAttribute<?> attribute, int transitionTicks) {
        Layer layer = layers.get(layerId);
        if (layer != null) {
            return layer.clearAttribute(attribute, transitionTicks);
        }
        return false;
    }

    private void addLayersTo(EnvironmentAttributeSystem.Builder system) {
        for (EnvironmentAttribute<?> attribute : modifiedAttributes) {
            addLayerTo(system, attribute);
        }
    }

    private <Value> void addLayerTo(EnvironmentAttributeSystem.Builder system, EnvironmentAttribute<Value> attribute) {
        Collection<Layer> layers = this.layers.values();
        system.addTimeBasedLayer(attribute, (baseValue, _) -> {
            Value result = baseValue;
            for (Layer layer : layers) {
                result = layer.apply(result, attribute);
            }
            return result;
        });
    }

    public Stream<Identifier> streamLayerIds() {
        return layers.keySet().stream();
    }

    @Override
    public void close() {
        decoratorHandle.close();
    }

    public void synchronizeTo(ServerPlayer player) {
        for (Map.Entry<Identifier, Layer> layer : layers.entrySet()) {
            PacketDistributor.sendToPlayer(player, new ClientboundEnvironmentAttributesPacket(
                    layer.getKey(),
                    layer.getValue().attributes.values().stream()
                            .map(LayerAttributeHolder::packForNetwork)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toUnmodifiableList()),
                    List.of(),
                    0
            ));
        }
    }

    public void synchronizeRemovalTo(ServerPlayer player) {
        for (Map.Entry<Identifier, Layer> layer : layers.entrySet()) {
            PacketDistributor.sendToPlayer(player, new ClientboundEnvironmentAttributesPacket(
                    layer.getKey(),
                    List.of(),
                    List.copyOf(layer.getValue().attributes.keySet()),
                    0
            ));
        }
    }

    private static class Layer {
        private final Map<EnvironmentAttribute<?>, LayerAttributeHolder<?>> attributes = new Reference2ObjectOpenHashMap<>();

        @SuppressWarnings("unchecked")
        private <Value> LayerAttributeHolder<Value> getOrCreateAttributeHolder(EnvironmentAttribute<Value> attribute) {
            return (LayerAttributeHolder<Value>) attributes.computeIfAbsent(attribute, LayerAttributeHolder::new);
        }

        @SuppressWarnings("unchecked")
        private <Value> @Nullable LayerAttributeHolder<Value> getAttributeHolder(EnvironmentAttribute<Value> attribute) {
            return (LayerAttributeHolder<Value>) attributes.get(attribute);
        }

        public <Value, Argument> void modifyAttribute(EnvironmentAttribute<Value> attribute, AttributeModifier<Value, Argument> modifier, Argument argument, int transitionTicks) {
            getOrCreateAttributeHolder(attribute).modify(modifier, argument, transitionTicks);
        }

        public boolean clearAttribute(EnvironmentAttribute<?> attribute, int transitionTicks) {
            LayerAttributeHolder<?> holder = getAttributeHolder(attribute);
            return holder != null && holder.clear(transitionTicks);
        }

        public <Value> Value apply(Value baseValue, EnvironmentAttribute<Value> attribute) {
            LayerAttributeHolder<Value> holder = getAttributeHolder(attribute);
            return holder == null ? baseValue : holder.apply(baseValue);
        }

        public boolean tick() {
            attributes.values().removeIf(LayerAttributeHolder::tick);
            return attributes.isEmpty();
        }
    }

    private static class LayerAttributeHolder<Value> {
        private final EnvironmentAttribute<Value> attribute;

        private @Nullable Modifier<Value, ?> activeModifier;
        private @Nullable Transition<Value> transition;

        private LayerAttributeHolder(EnvironmentAttribute<Value> attribute) {
            this.attribute = attribute;
        }

        public <Argument> void modify(AttributeModifier<Value, Argument> modifier, Argument argument, int transitionTicks) {
            startTransition(new Modifier<>(modifier, argument, 1.0f), transitionTicks);
        }

        public boolean clear(int transitionTicks) {
            if (activeModifier == null) {
                return false;
            }
            startTransition(null, transitionTicks);
            return true;
        }

        private <Argument> void startTransition(@Nullable Modifier<Value, Argument> modifier, int transitionTicks) {
            Transition<Value> newTransition = new Transition<>(transitionTicks);
            if (transition != null) {
                float currentAlpha = transition.currentAlpha();
                for (Modifier<Value, ?> oldModifier : transition.decayingModifiers) {
                    newTransition.decayingModifiers.add(oldModifier.scaleWeight(1.0f - currentAlpha));
                }
                if (activeModifier != null) {
                    newTransition.decayingModifiers.add(activeModifier.scaleWeight(currentAlpha));
                }
            } else if (activeModifier != null) {
                newTransition.decayingModifiers.add(activeModifier);
            }
            transition = newTransition;
            activeModifier = modifier;
        }

        public Value apply(Value baseValue) {
            Value targetValue = activeModifier != null ? activeModifier.apply(baseValue) : baseValue;
            if (transition == null) {
                return targetValue;
            }
            LerpFunction<Value> lerpFunction = attribute.type().spatialLerp();
            Value sourceValue = computeDecayingValue(transition, baseValue, lerpFunction);
            return lerpFunction.apply(transition.currentAlpha(), sourceValue, targetValue);
        }

        private Value computeDecayingValue(Transition<Value> transition, Value baseValue, LerpFunction<Value> lerpFunction) {
            Value resultValue = null;
            float accumulatedWeight = 0.0f;
            for (Modifier<Value, ?> modifier : transition.decayingModifiers) {
                Value modifierValue = modifier.apply(baseValue);
                float modifierWeight = modifier.weight;
                accumulatedWeight += modifierWeight;
                if (resultValue == null) {
                    resultValue = modifierValue;
                } else {
                    float relativeFraction = modifierWeight / accumulatedWeight;
                    resultValue = lerpFunction.apply(relativeFraction, resultValue, modifierValue);
                }
            }
            return resultValue != null ? resultValue : baseValue;
        }

        public boolean tick() {
            if (transition != null && transition.tick()) {
                transition = null;
            }
            return activeModifier == null && transition == null;
        }

        public ClientboundEnvironmentAttributesPacket.@Nullable Modifier<Value, ?> packForNetwork() {
            if (!attribute.isSyncable()) {
                return null;
            }
            // Ideally we'd synchronize partial transitions, but avoiding the complexity for now
            if (activeModifier == null) {
                return null;
            }
            return activeModifier.packForNetwork(attribute);
        }

        private static class Transition<Value> {
            private final List<Modifier<Value, ?>> decayingModifiers = new ArrayList<>();

            private int currentTicks;
            private final int totalTicks;

            private Transition(int totalTicks) {
                this.totalTicks = totalTicks;
            }

            public boolean tick() {
                if (currentTicks < totalTicks) {
                    currentTicks++;
                    return false;
                } else {
                    return true;
                }
            }

            public float currentAlpha() {
                return totalTicks == 0 ? 1.0f : (float) currentTicks / totalTicks;
            }
        }

        private record Modifier<Value, Argument>(
                AttributeModifier<Value, Argument> modifier,
                Argument argument,
                float weight
        ) {
            public Value apply(Value baseValue) {
                return modifier.apply(baseValue, argument);
            }

            public Modifier<Value, Argument> scaleWeight(float factor) {
                return new Modifier<>(modifier, argument, weight * factor);
            }

            public ClientboundEnvironmentAttributesPacket.Modifier<Value, Argument> packForNetwork(EnvironmentAttribute<Value> attribute) {
                return new ClientboundEnvironmentAttributesPacket.Modifier<>(attribute, modifier, argument);
            }
        }
    }
}
