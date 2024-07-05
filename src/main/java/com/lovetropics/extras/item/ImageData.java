package com.lovetropics.extras.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;

import java.util.List;
import java.util.Optional;

public record ImageData(Optional<Component> name, ResourceLocation texture, float width, float height, float offsetX, float offsetY, List<TextElement> text) {
    public static final Codec<ImageData> CODEC = RecordCodecBuilder.create(i -> i.group(
            ComponentSerialization.CODEC.optionalFieldOf("name").forGetter(ImageData::name),
            ResourceLocation.CODEC.fieldOf("texture").forGetter(ImageData::texture),
            Codec.FLOAT.fieldOf("width").forGetter(ImageData::width),
            Codec.FLOAT.fieldOf("height").forGetter(ImageData::height),
            Codec.FLOAT.optionalFieldOf("offset_x", 0.0f).forGetter(ImageData::offsetX),
            Codec.FLOAT.optionalFieldOf("offset_y", 0.0f).forGetter(ImageData::offsetY),
            TextElement.CODEC.listOf().optionalFieldOf("text", List.of()).forGetter(ImageData::text)
    ).apply(i, ImageData::new));

    public ImageData(final Component name, final ResourceLocation texture, final float width, final float height, final List<TextElement> text) {
        this(Optional.of(name), texture, width, height, 0.0f, 0.0f, text);
    }

    public ImageData(final Component name, final ResourceLocation texture, final float width, final float height) {
        this(Optional.of(name), texture, width, height, 0.0f, 0.0f, List.of());
    }

    public static TextElement text(final Component text, final float x, final float y) {
        return new TextElement(text, x, y, Float.MAX_VALUE, TextElement.DEFAULT_LINE_SPACING, Align.START, Align.START);
    }

    public record TextElement(Component text, float x, float y, float maxWidth, float lineSpacing, Align alignHorizontal, Align alignVertical) {
        private static final float DEFAULT_LINE_SPACING = 9;

        public static final Codec<TextElement> CODEC = RecordCodecBuilder.create(i -> i.group(
                ComponentSerialization.CODEC.fieldOf("text").forGetter(TextElement::text),
                Codec.FLOAT.fieldOf("x").forGetter(TextElement::x),
                Codec.FLOAT.fieldOf("y").forGetter(TextElement::y),
                Codec.FLOAT.optionalFieldOf("max_width", Float.MAX_VALUE).forGetter(TextElement::maxWidth),
                Codec.FLOAT.optionalFieldOf("line_spacing", DEFAULT_LINE_SPACING).forGetter(TextElement::lineSpacing),
                Align.CODEC.fieldOf("align_horizontal").forGetter(TextElement::alignHorizontal),
                Align.CODEC.fieldOf("align_vertical").forGetter(TextElement::alignVertical)
        ).apply(i, TextElement::new));

        public TextElement align(final Align horizontal, final Align vertical) {
            return new TextElement(text, x, y, maxWidth, lineSpacing, horizontal, vertical);
        }

        public TextElement maxWidth(final float maxWidth) {
            return new TextElement(text, x, y, maxWidth, lineSpacing, alignHorizontal, alignVertical);
        }

        public TextElement lineSpacing(final float lineSpacing) {
            return new TextElement(text, x, y, maxWidth, lineSpacing, alignHorizontal, alignVertical);
        }
    }

    public enum Align implements StringRepresentable {
        START("start"),
        CENTER("center"),
        END("end"),
        ;

        public static final Codec<Align> CODEC = StringRepresentable.fromEnum(Align::values);

        private final String name;

        Align(final String name) {
            this.name = name;
        }

        public float resolve(final float min, final float size) {
            return switch (this) {
                case START -> min;
                case CENTER -> min - size / 2.0f;
                case END -> min - size;
            };
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
