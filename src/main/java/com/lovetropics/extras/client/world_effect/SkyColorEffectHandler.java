package com.lovetropics.extras.client.world_effect;

import com.lovetropics.extras.LTExtras;
import net.minecraft.client.Minecraft;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = LTExtras.MODID, value = Dist.CLIENT)
public class SkyColorEffectHandler {
    private static final EffectInterpolator<State> INTERPOLATOR = new EffectInterpolator<>(State::lerp, State.NONE);

    @SubscribeEvent
    public static void onClientTick(final ClientTickEvent.Pre event) {
        final Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null) {
            INTERPOLATOR.step();
        } else {
            INTERPOLATOR.reset(State.NONE);
        }
    }

    public static Vec3 modifyColor(final Vec3 color, final float partialTicks) {
        final State frameState = INTERPOLATOR.get(partialTicks);
        if (frameState.alpha == 0.0f) {
            return color;
        } else if (frameState.alpha == 1.0f) {
            return frameState.color;
        }
        return color.lerp(frameState.color, frameState.alpha);
    }

    public static void apply(final int color, final int fadeLength) {
        INTERPOLATOR.setTarget(new State(
                new Vec3(
                        FastColor.ARGB32.red(color) / 255.0,
                        FastColor.ARGB32.green(color) / 255.0,
                        FastColor.ARGB32.blue(color) / 255.0
                ),
                1.0f
        ), fadeLength);
    }

    public static void clear(final int fadeLength) {
        INTERPOLATOR.setTarget(State.NONE, fadeLength);
    }

    private record State(Vec3 color, float alpha) {
        public static final State NONE = new State(Vec3.ZERO, 0.0f);

        public State lerp(final State target, final float x) {
            return new State(
                    color.lerp(target.color, x),
                    Mth.lerp(x, alpha, target.alpha)
            );
        }
    }
}
