package com.lovetropics.extras.model_modifer;

public interface ModelModifier<T extends ModelModifier<?>> {
    ModelModifierType<T> type();
}
