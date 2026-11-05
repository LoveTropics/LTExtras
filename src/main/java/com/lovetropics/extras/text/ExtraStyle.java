package com.lovetropics.extras.text;

import com.lovetropics.extras.duck.ExtendedStyle;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;

import java.util.Optional;

public record ExtraStyle(
        Optional<Identifier> background
) implements FontDescription {
    public static final ExtraStyle EMPTY = new ExtraStyle(Optional.empty());

    public static final MapCodec<ExtraStyle> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Identifier.CODEC.optionalFieldOf("background").forGetter(ExtraStyle::background)
    ).apply(i, ExtraStyle::new));

    public static MapCodec<Style> wrapCodec(MapCodec<Style> codec) {
        return Codec.mapPair(codec, MAP_CODEC).xmap(
                pair -> pair.getSecond().applyTo(pair.getFirst()),
                style -> {
                    ExtendedStyle extended = (ExtendedStyle) (Object) style;
                    ExtraStyle extra = extended.lt$getExtraStyle();
                    if (!extra.equals(EMPTY)) {
                        return Pair.of(ExtraStyle.EMPTY.applyTo(style), extra);
                    } else {
                        return Pair.of(style, ExtraStyle.EMPTY);
                    }
                }
        );
    }

    public static ExtraStyle get(Style style) {
        return ((ExtendedStyle) (Object) style).lt$getExtraStyle();
    }

    public Style applyTo(Style style) {
        return ((ExtendedStyle) (Object) style).lt$withExtraStyle(this);
    }
}
