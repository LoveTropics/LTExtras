package com.lovetropics.extras.mixin.text;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.lovetropics.extras.duck.ExtendedStyle;
import com.lovetropics.extras.text.ExtraStyle;
import com.lovetropics.extras.text.ExtraStyleFontDescription;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import org.jspecify.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Style.class)
public abstract class StyleMixin implements ExtendedStyle {
    @Shadow
    @Final
    private @Nullable TextColor color;
    @Shadow
    @Final
    private @Nullable Integer shadowColor;
    @Shadow
    @Final
    private @Nullable Boolean bold;
    @Shadow
    @Final
    private @Nullable Boolean italic;
    @Shadow
    @Final
    private @Nullable Boolean underlined;
    @Shadow
    @Final
    private @Nullable Boolean strikethrough;
    @Shadow
    @Final
    private @Nullable Boolean obfuscated;
    @Shadow
    @Final
    private @Nullable ClickEvent clickEvent;
    @Shadow
    @Final
    private @Nullable HoverEvent hoverEvent;
    @Shadow
    @Final
    private @Nullable String insertion;
    @Shadow
    @Final
    private @Nullable FontDescription font;

    @Invoker("<init>")
    private static Style invokeCreate(@Nullable TextColor color, @Nullable Integer shadowColor, @Nullable Boolean bold, @Nullable Boolean italic, @Nullable Boolean underlined, @Nullable Boolean strikethrough, @Nullable Boolean obfuscated, @Nullable ClickEvent clickEvent, @Nullable HoverEvent hoverEvent, @Nullable String insertion, @Nullable FontDescription font) {
        throw new UnsupportedOperationException();
    }

    @WrapOperation(method = "getFont", at = @At(value = "FIELD", target = "Lnet/minecraft/network/chat/Style;font:Lnet/minecraft/network/chat/FontDescription;", opcode = Opcodes.GETFIELD))
    private @Nullable FontDescription getFont(Style style, Operation<@Nullable FontDescription> original) {
        return font instanceof ExtraStyleFontDescription extraDesc ? extraDesc.delegate() : font;
    }

    @WrapOperation(method = "withFont", at = @At(value = "FIELD", target = "Lnet/minecraft/network/chat/Style;font:Lnet/minecraft/network/chat/FontDescription;", opcode = Opcodes.GETFIELD))
    private @Nullable FontDescription getFontWithFont(Style style, Operation<@Nullable FontDescription> original) {
        return font instanceof ExtraStyleFontDescription extraDesc ? extraDesc.delegate() : font;
    }

    @WrapOperation(method = "withFont", at = @At(value = "NEW", target = "(Lnet/minecraft/network/chat/TextColor;Ljava/lang/Integer;Ljava/lang/Boolean;Ljava/lang/Boolean;Ljava/lang/Boolean;Ljava/lang/Boolean;Ljava/lang/Boolean;Lnet/minecraft/network/chat/ClickEvent;Lnet/minecraft/network/chat/HoverEvent;Ljava/lang/String;Lnet/minecraft/network/chat/FontDescription;)Lnet/minecraft/network/chat/Style;"))
    private Style createWithFont(TextColor color, Integer shadowColor, Boolean bold, Boolean italic, Boolean underlined, Boolean strikethrough, Boolean obfuscated, ClickEvent clickEvent, HoverEvent hoverEvent, String insertion, FontDescription font, Operation<Style> original) {
        ExtraStyle extra = lt$getExtraStyle();
        FontDescription wrappedFont;
        if (extra.equals(ExtraStyle.EMPTY)) {
            wrappedFont = font;
        } else {
            wrappedFont = new ExtraStyleFontDescription(font, extra);
        }
        return original.call(color, shadowColor, bold, italic, underlined, strikethrough, obfuscated, clickEvent, hoverEvent, insertion, wrappedFont);
    }

    @Override
    public Style lt$withExtraStyle(ExtraStyle style) {
        ExtraStyleFontDescription newFont;
        if (font instanceof ExtraStyleFontDescription desc) {
            newFont = new ExtraStyleFontDescription(desc.delegate(), style);
        } else {
            newFont = new ExtraStyleFontDescription(font != null ? font : FontDescription.DEFAULT, style);
        }
        return invokeCreate(color, shadowColor, bold, italic, underlined, strikethrough, obfuscated, clickEvent, hoverEvent, insertion, newFont);
    }

    @Override
    public ExtraStyle lt$getExtraStyle() {
        if (font instanceof ExtraStyleFontDescription extraDesc) {
            return extraDesc.extra();
        }
        return ExtraStyle.EMPTY;
    }
}
