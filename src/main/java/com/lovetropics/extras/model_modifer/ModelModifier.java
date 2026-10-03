package com.lovetropics.extras.model_modifer;

public interface ModelModifier {
    ModelModifierType<? extends ModelModifier> type();
}
