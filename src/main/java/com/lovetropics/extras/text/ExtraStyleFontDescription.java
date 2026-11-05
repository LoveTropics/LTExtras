package com.lovetropics.extras.text;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.FontDescription;

/// This is a totally stupid way to extend [net.minecraft.network.chat.Style] - but it's not really practical to modify every constructor.
public record ExtraStyleFontDescription(
        FontDescription delegate,
        ExtraStyle extra
) implements FontDescription {
    public static final Codec<ExtraStyleFontDescription> CODEC = Codec.lazyInitialized(() -> RecordCodecBuilder.create(i -> i.group(
            FontDescription.CODEC.fieldOf("delegate").forGetter(ExtraStyleFontDescription::delegate),
            ExtraStyle.MAP_CODEC.forGetter(ExtraStyleFontDescription::extra)
    ).apply(i, ExtraStyleFontDescription::new)));

    public static Codec<FontDescription> wrapCodec(Codec<FontDescription> codec) {
        return Codec.either(CODEC, codec).xmap(
                Either::unwrap,
                desc -> desc instanceof ExtraStyleFontDescription extra ? Either.left(extra) : Either.right(desc)
        );
    }
}
