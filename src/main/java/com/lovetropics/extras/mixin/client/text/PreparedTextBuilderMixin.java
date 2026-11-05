package com.lovetropics.extras.mixin.client.text;

import com.llamalad7.mixinextras.sugar.Local;
import com.lovetropics.extras.client.text.TextBackgroundRenderable;
import com.lovetropics.extras.text.ExtraStyle;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.TextRenderable;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Mixin(targets = "net.minecraft.client.gui.Font$PreparedTextBuilder")
public class PreparedTextBuilderMixin {
    @Shadow
    @Final
    Font this$0;
    @Shadow
    private float x;
    @Shadow
    private float y;

    @Unique
    private @Nullable Identifier lt$buildingBackground;
    @Unique
    private float lt$backgroundStartX;
    @Unique
    private @Nullable List<TextRenderable> lt$backgroundElements;

    @Inject(method = "accept(ILnet/minecraft/network/chat/Style;Lnet/minecraft/client/gui/font/glyphs/BakedGlyph;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/font/glyphs/BakedGlyph;createGlyph(FFIILnet/minecraft/network/chat/Style;FF)Lnet/minecraft/client/gui/font/TextRenderable$Styled;"))
    private void accept(int position, Style style, BakedGlyph glyph, CallbackInfoReturnable<Boolean> cir, @Local(name = "advance") float advance) {
        ExtraStyle extra = ExtraStyle.get(style);
        lt$updateBackground(extra.background().orElse(null));
    }

    @Inject(method = "visit", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/Font$PreparedTextBuilder;glyphs:Ljava/util/List;", opcode = Opcodes.GETFIELD))
    private void visit(Font.GlyphVisitor visitor, CallbackInfo ci) {
        // We might have run out of text, but never ended the background - make sure to end it now
        lt$updateBackground(null);
        if (lt$backgroundElements != null) {
            for (TextRenderable element : lt$backgroundElements) {
                visitor.acceptEffect(element);
            }
        }
    }

    @Unique
    private void lt$updateBackground(@Nullable Identifier backgroundSprite) {
        if (Objects.equals(lt$buildingBackground, backgroundSprite)) {
            return;
        }

        if (backgroundSprite != null) {
            lt$backgroundStartX = x;
            lt$buildingBackground = backgroundSprite;
        } else {
            float startX = lt$backgroundStartX;
            float endX = x;
            if (lt$backgroundElements == null) {
                lt$backgroundElements = new ArrayList<>();
            }
            lt$backgroundElements.add(new TextBackgroundRenderable(
                    lt$buildingBackground,
                    startX - 2.0f,
                    y - 1.0f,
                    endX + 1.0f,
                    y + this$0.lineHeight,
                    0.0f
            ));
            lt$buildingBackground = null;
        }
    }
}
