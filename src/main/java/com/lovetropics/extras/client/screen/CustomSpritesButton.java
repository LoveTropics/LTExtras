package com.lovetropics.extras.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.util.Mth;

public class CustomSpritesButton extends Button {
	private final WidgetSprites sprites;

	public CustomSpritesButton(WidgetSprites sprites, Builder builder) {
		super(builder);
		this.sprites = sprites;
	}

	@Override
	protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		graphics.blitSprite(sprites.get(active, isHoveredOrFocused()), getX(), getY(), getWidth(), getHeight());
		renderString(graphics, Minecraft.getInstance().font, getFGColor() | Mth.ceil(alpha * 255.0F) << 24);
	}
}
