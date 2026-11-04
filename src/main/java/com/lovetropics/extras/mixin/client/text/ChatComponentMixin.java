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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChatComponent.class)
public class ChatComponentMixin {
    @WrapOperation(method = "lambda$extractRenderState$1", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent$ChatGraphicsAccess;fill(IIIII)V"))
    private static void fillLineBackground(ChatComponent.ChatGraphicsAccess chatGraphics, int x0, int y0, int x1, int y1, int color, Operation<Void> original, @Local(argsOnly = true, name = "var5x") GuiMessage.Line line, @Local(argsOnly = true, name = "alphax") float alpha) {
        if (chatGraphics instanceof ExtendedChatGraphicsAccess access && line.parent().content().getContents() instanceof ChatHighlightContents(int leftColor, int rightColor)) {
            GuiGraphicsExtractor graphics = access.lt$getGraphics();
            lt$horizontalGradient(graphics, x0, y0, x1, y1, ARGB.multiplyAlpha(leftColor, alpha), ARGB.multiplyAlpha(rightColor, alpha));
            lt$horizontalGradient(graphics, x0, y0, x1, y0 + 1, ARGB.multiplyAlpha(leftColor, alpha), ARGB.multiplyAlpha(rightColor, alpha));
            lt$horizontalGradient(graphics, x0, y1 - 1, x1, y1, ARGB.multiplyAlpha(leftColor, alpha), ARGB.multiplyAlpha(rightColor, alpha));
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
}
