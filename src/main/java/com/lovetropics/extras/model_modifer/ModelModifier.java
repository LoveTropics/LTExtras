package com.lovetropics.extras.model_modifer;

public interface ModelModifier<T extends ModelModifier<?>> {

    T data();

    ModelModifierType<T> type();

}
