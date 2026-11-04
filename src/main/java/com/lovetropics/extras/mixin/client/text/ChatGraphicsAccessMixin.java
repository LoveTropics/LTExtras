package com.lovetropics.extras.mixin.client.text;

import com.lovetropics.extras.duck.ExtendedChatGraphicsAccess;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = {"net.minecraft.client.gui.components.ChatComponent$DrawingFocusedGraphicsAccess", "net.minecraft.client.gui.components.ChatComponent$DrawingBackgroundGraphicsAccess"})
public class ChatGraphicsAccessMixin implements ExtendedChatGraphicsAccess {
    @Shadow(remap = false)
    @Final
    private GuiGraphicsExtractor graphics;

    @Override
    public GuiGraphicsExtractor lt$getGraphics() {
        return graphics;
    }
}
