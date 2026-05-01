package com.lovetropics.extras.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.ARGB;

public class CustomSpritesButton extends Button {
    private final WidgetSprites sprites;

    public CustomSpritesButton(WidgetSprites sprites, Builder builder) {
        super(builder);
        this.sprites = sprites;
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprites.get(active, isHoveredOrFocused()), getX(), getY(), getWidth(), getHeight(), alpha);
//        renderString(graphics, Minecraft.getInstance().font, ARGB.color(alpha, getFGColor())); // Todo 26.1 Port
    }

    @Override
    public int getFGColor() {
        return super.getFGColor();
    }
}
