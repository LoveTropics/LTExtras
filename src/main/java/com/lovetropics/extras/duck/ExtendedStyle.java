package com.lovetropics.extras.duck;

import com.lovetropics.extras.text.ExtraStyle;
import net.minecraft.network.chat.Style;

public interface ExtendedStyle {
    Style lt$withExtraStyle(ExtraStyle style);

    ExtraStyle lt$getExtraStyle();
}
