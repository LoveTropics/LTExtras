package com.lovetropics.extras.client.world_effect;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.Mth;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.jspecify.annotations.Nullable;

@EventBusSubscriber(Dist.CLIENT)
public class EnvironmentAttributeEffectHandler {

   private static final EffectInterpolator<State> INTERPOLATOR = new EffectInterpolator<>(State::lerp, State.NONE);

    @Nullable
    private static EnvironmentAttributeMap map;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event) {
//            Minecraft minecraft = Minecraft.getInstance();
//            if (minecraft.level != null) {
//                INTERPOLATOR.step();
//            } else {
//                INTERPOLATOR.reset(State.NONE);
//            }
    }

//    public static Vec3 modifyColor(Vec3 color, float partialTicks) {
//        State frameState = INTERPOLATOR.get(partialTicks);
//        if (frameState.alpha == 0.0f) {
//            return color;
//        } else if (frameState.alpha == 1.0f) {
//            return frameState.color;
//        }
//        return color.lerp(frameState.color, frameState.alpha);
//    }

    public static void apply(EnvironmentAttributeMap map, int fadeLength) {
        EnvironmentAttributeEffectHandler.map = map;
//        INTERPOLATOR.setTarget(new State(
//                new Vec3(ARGB.redFloat(color), ARGB.greenFloat(color), ARGB.blueFloat(color)),
//                1.0f
//        ), fadeLength);
    }

    public static void clear(int fadeLength) {
        EnvironmentAttributeEffectHandler.map = null;
//        INTERPOLATOR.setTarget(State.NONE, fadeLength);
    }

    private record State(Vec3 color, float alpha) {
        public static final State NONE = new State(Vec3.ZERO, 0.0f);

        public State lerp(State target, float x) {
            return new State(
                    color.lerp(target.color, x),
                    Mth.lerp(x, alpha, target.alpha)
            );
        }
    }

    public static void inject(ClientLevel level, EnvironmentAttributeSystem.Builder environmentAttributes) {
        for (EnvironmentAttribute<?> environmentAttribute : level.registryAccess().lookupOrThrow(Registries.ENVIRONMENT_ATTRIBUTE)) {
            if (!environmentAttribute.isSyncable()) {
                continue;
            }
            addLayerTyped(environmentAttributes, environmentAttribute);
        }
    }

    private static <T> void addLayerTyped(EnvironmentAttributeSystem.Builder builder, EnvironmentAttribute<T> attribute) {
        builder.addConstantLayer(attribute, baseValue -> {
            if (map != null) {
                EnvironmentAttributeMap.Entry<T, ?> entry = map.get(attribute);
                if (entry != null) {
                    return entry.applyModifier(baseValue);
                }
            }
            return baseValue;
        });
    }

}
