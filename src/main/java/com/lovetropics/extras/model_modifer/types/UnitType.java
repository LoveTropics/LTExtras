package com.lovetropics.extras.model_modifer.types;

import com.lovetropics.extras.model_modifer.ModelModifier;
import com.lovetropics.extras.model_modifer.ModelModifierType;

public abstract class UnitType implements ModelModifierType<UnitType>, ModelModifier<UnitType> {

    @Override
    public UnitType data() {
        return this;
    }
}
