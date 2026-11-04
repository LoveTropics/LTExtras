package com.lovetropics.extras.text;

import com.lovetropics.lib.text.ChatHighlighter;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.ExtraCodecs;
import org.jspecify.annotations.Nullable;

public record ChatHighlightContents(
        int leftColor,
        int rightColor
) implements ComponentContents {
    public static final MapCodec<ChatHighlightContents> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ExtraCodecs.STRING_ARGB_COLOR.fieldOf("left_highlight").forGetter(ChatHighlightContents::leftColor),
            ExtraCodecs.STRING_ARGB_COLOR.fieldOf("right_highlight").forGetter(ChatHighlightContents::rightColor)
    ).apply(i, ChatHighlightContents::new));

    static {
        ChatHighlighter.bindAccess(new ChatHighlighter.Access() {
            @Override
            public Component highlight(Component message, int leftColor, int rightColor) {
                return MutableComponent.create(new ChatHighlightContents(leftColor, rightColor)).append(message);
            }

            @Override
            public ChatHighlighter.@Nullable Highlight extract(Component message) {
                if (message.getContents() instanceof ChatHighlightContents(int leftColor, int rightColor)) {
                    return new ChatHighlighter.Highlight(leftColor, rightColor);
                }
                return null;
            }
        });
    }

    @Override
    public MapCodec<ChatHighlightContents> codec() {
        return CODEC;
    }
}
