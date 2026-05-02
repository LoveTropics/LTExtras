package com.lovetropics.extras.client.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.RenderPipelines;

public class CustomSpritesButton extends Button {
    private final WidgetSprites sprites;

    public CustomSpritesButton(WidgetSprites sprites, Builder builder) {
        super(builder);
        this.sprites = sprites;
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprites.get(active, isHoveredOrFocused()), getX(), getY(), getWidth(), getHeight(), alpha);
        extractDefaultLabel(graphics.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.NONE));
    }
}
