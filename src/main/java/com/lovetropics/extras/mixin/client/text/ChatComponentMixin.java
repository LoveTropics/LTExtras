package com.lovetropics.extras.mixin.client.text;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.lovetropics.extras.client.screen.render.GradientRectElement;
import com.lovetropics.extras.duck.ExtendedChatGraphicsAccess;
import com.lovetropics.extras.text.ChatHighlightContents;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.ARGB;
import org.joml.Matrix3x2f;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(ChatComponent.class)
public class ChatComponentMixin {
    @Shadow
    @Final
    private List<GuiMessage.Line> trimmedMessages;
    @Shadow
    private int chatScrollbarPos;

    @Unique
    private static final ScopedValue<@Nullable CapturedState> LT$CAPTURED_STATE = ScopedValue.newInstance();

    @WrapOperation(method = "extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/gui/Font;IIILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent;extractRenderState(Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;IILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;)V"))
    private void extractRenderState(ChatComponent chat, ChatComponent.ChatGraphicsAccess graphics, int screenHeight, int ticks, ChatComponent.DisplayMode displayMode, Operation<Void> original) {
        ScopedValue.where(LT$CAPTURED_STATE, new CapturedState(trimmedMessages, chatScrollbarPos)).run(() ->
                original.call(chat, graphics, screenHeight, ticks, displayMode)
        );
    }

    @WrapOperation(method = "lambda$extractRenderState$1", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;fill(IIIII)V"))
    private static void fillLineBackground(ChatComponent.ChatGraphicsAccess chatGraphics, int x0, int y0, int x1, int y1, int color, Operation<Void> original, @Local(argsOnly = true, name = "var5x") GuiMessage.Line line, @Local(argsOnly = true, name = "lineIndex") int lineIndex, @Local(argsOnly = true, name = "alphax") float alpha) {
        if (chatGraphics instanceof ExtendedChatGraphicsAccess access && line.parent().content().getContents() instanceof ChatHighlightContents highlight) {
            GuiGraphicsExtractor graphics = access.lt$getGraphics();

            int leftColor = highlight.leftColor();
            int rightColor = highlight.rightColor();
            lt$horizontalGradient(graphics, x0, y0, x1, y1, ARGB.multiplyAlpha(leftColor, alpha), ARGB.multiplyAlpha(rightColor, alpha));
            if (!lt$isSameHighlight(lineIndex, 1, highlight)) {
                lt$horizontalGradient(graphics, x0, y0, x1, y0 + 1, ARGB.multiplyAlpha(leftColor, alpha), ARGB.multiplyAlpha(rightColor, alpha));
            }
            if (!lt$isSameHighlight(lineIndex, -1, highlight)) {
                lt$horizontalGradient(graphics, x0, y1 - 1, x1, y1, ARGB.multiplyAlpha(leftColor, alpha), ARGB.multiplyAlpha(rightColor, alpha));
            }
        } else {
            original.call(chatGraphics, x0, y0, x1, y1, color);
        }
    }

    @Unique
    private static void lt$horizontalGradient(GuiGraphicsExtractor graphics, int x0, int y0, int x1, int y1, int leftColor, int rightColor) {
        graphics.submitGuiElementRenderState(new GradientRectElement(
                RenderPipelines.GUI,
                TextureSetup.noTexture(),
                new Matrix3x2f(graphics.pose()),
                x0, y0, x1, y1,
                leftColor, rightColor,
                graphics.peekScissorStack()
        ));
    }

    @Unique
    private static boolean lt$isSameHighlight(int index, int offset, ChatHighlightContents highlight) {
        CapturedState capturedState = LT$CAPTURED_STATE.get();
        if (capturedState == null) {
            return false;
        }
        int lineIndex = index + offset + capturedState.chatScrollbarPos;
        GuiMessage.Line line = lineIndex >= 0 && lineIndex < capturedState.trimmedMessages.size() ? capturedState.trimmedMessages.get(lineIndex) : null;
        return line != null && highlight.equals(line.parent().content().getContents());
    }

    private record CapturedState(
            List<GuiMessage.Line> trimmedMessages,
            int chatScrollbarPos
    ) {
    }
}
