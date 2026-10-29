package com.lovetropics.extras.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStackTemplate;

public record StackedHat(
        ItemStackTemplate item,
        float width,
        float height,
        float bounciness
) {
    public static final Codec<StackedHat> CODEC = RecordCodecBuilder.create(i -> i.group(
            ItemStackTemplate.MAP_CODEC.forGetter(StackedHat::item),
            Codec.FLOAT.optionalFieldOf("width", 1.0f).forGetter(StackedHat::width),
            Codec.FLOAT.optionalFieldOf("height", 1.0f).forGetter(StackedHat::height),
            Codec.FLOAT.optionalFieldOf("bounciness", 1.0f).forGetter(StackedHat::bounciness)
    ).apply(i, StackedHat::new));
}
