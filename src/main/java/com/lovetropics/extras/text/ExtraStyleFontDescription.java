package com.lovetropics.extras.text;

import net.minecraft.network.chat.FontDescription;

/// This is a totally stupid way to extend [net.minecraft.network.chat.Style] - but it's not really practical to modify every constructor.
public record ExtraStyleFontDescription(
        FontDescription delegate,
        ExtraStyle extra
) implements FontDescription {
}
