package com.lovetropics.extras.client.world_effect;

import java.util.Objects;

public class EffectInterpolator<T> {
    private final Function<T> function;
    private T lastState;
    private T state;
    private T targetState;
    private int interpolationSteps;

    public EffectInterpolator(final Function<T> function, final T defaultState) {
        this.function = function;
        lastState = state = targetState = defaultState;
    }

    public void step() {
        lastState = state;
        if (interpolationSteps > 0) {
            final int steps = interpolationSteps--;
            if (steps != 1) {
                state = function.apply(state, targetState, 1.0f / steps);
            } else {
                state = targetState;
            }
        }
    }

    public void setTarget(final T state, final int steps) {
        targetState = state;
        interpolationSteps = steps;
        if (steps == 0) {
            this.state = lastState = state;
        }
    }

    public T get(final float partialTicks) {
        if (Objects.equals(state, lastState)) {
            return state;
        }
        return function.apply(lastState, state, partialTicks);
    }

    public interface Function<T> {
        T apply(T from, T to, float alpha);
    }
}
