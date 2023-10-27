package com.lovetropics.extras.client.screen.map;

import com.lovetropics.extras.client.ClientMapPoiManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.resources.ResourceLocation;

class PoiImageButton extends ImageButton {
    private final String poiName;
    private final boolean isGm;
    private boolean shouldRender;

    public PoiImageButton(String poiName, boolean isGm, int pX, int pY, int pWidth, int pHeight, int pXTexStart, int pYTexStart, int pYDiffTex, ResourceLocation pResourceLocation, int pTextureWidth, int pTextureHeight, OnPress pOnPress) {
        super(pX, pY, pWidth, pHeight, pXTexStart, pYTexStart, pYDiffTex, pResourceLocation, pTextureWidth, pTextureHeight, pOnPress);
        this.poiName = poiName;
        this.isGm = isGm;
        this.shouldRender = shouldRender();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (pPartialTick % 20 == 0) {
            shouldRender = shouldRender();
        }

        if (shouldRender) {
            super.render(guiGraphics, pMouseX, pMouseY, pPartialTick);
        }
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (shouldRender) {
            super.renderWidget(guiGraphics, pMouseX, pMouseY, pPartialTick);
        }
    }

    @Override
    public void renderTexture(GuiGraphics guiGraphics, ResourceLocation pTexture, int pX, int pY, int pUOffset, int pVOffset, int p_283472_, int pWidth, int pHeight, int pTextureWidth, int pTextureHeight) {
        if (shouldRender) {
            super.renderTexture(guiGraphics, pTexture, pX, pY, pUOffset, pVOffset, p_283472_, pWidth, pHeight, pTextureWidth, pTextureHeight);
        }
    }

    private boolean shouldRender() {
        return ClientMapPoiManager.getPois().get(poiName).enabled() || isGm;
    }

}