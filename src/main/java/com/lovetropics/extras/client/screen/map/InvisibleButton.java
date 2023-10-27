package com.lovetropics.extras.client.screen.map;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;

class InvisibleButton extends Button {
    protected InvisibleButton(final Builder builder) {
        super(builder);
    }

    @Override
    protected void renderWidget(final GuiGraphics guiGraphics, final int mouseX, final int mouseY, final float partialTick) {
        //Intentionally left blank
    }
}