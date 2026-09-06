package com.lovetropics.extras.duck;

import net.minecraft.world.attribute.EnvironmentAttributeSystem;

public interface HasDynamicEas {
    DecoratorHandle ltextras$addEasDecorator(Decorator decorator);

    @FunctionalInterface
    interface Decorator {
        void addTo(EnvironmentAttributeSystem.Builder system);
    }

    interface DecoratorHandle extends AutoCloseable {
        void rebuild();

        @Override
        void close();
    }
}
