package com.lovetropics.extras.client.screen.map;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.client.map.ClientMapManager;
import com.lovetropics.extras.client.map.ClientPoi;
import com.lovetropics.extras.data.poi.PoiConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CommonColors;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.UUID;

class PoiButton extends AbstractButton {
    private static final int ICON_SIZE = 16;
    private static final int HALF_ICON_SIZE = ICON_SIZE / 2;
    private static final int BORDER_SIZE = 3;
    private static final int SIZE = ICON_SIZE + BORDER_SIZE * 2;
    private static final int TOOLTIP_HEIGHT = 18;

    private static final int HOVER_ANIMATION_LENGTH = 8;

    private static final ResourceLocation TOOLTIP_SPRITE = LTExtras.location("widget/poi_tooltip");
    private static final ResourceLocation BACKGROUND_SPRITE = LTExtras.location("widget/poi_background");
    private static final ResourceLocation TITLE_BOX_SPRITE = ResourceLocation.withDefaultNamespace("advancements/title_box");

    private static final int SELECTED_Z_OFFSET = 250;

    private final ClientPoi poi;
    private final Font font;
    private final Runnable action;

    private int lastFocusAnimation;
    private int focusAnimation;

    private PoiButton(Font font, int x, int y, Component message, ClientPoi poi, Runnable action) {
        super(x, y, SIZE, SIZE, message);
        this.poi = poi;
        this.font = font;
        this.action = action;
    }

    public static PoiButton create(Font font, int x, int y, ClientPoi poi, Runnable action) {
        Component description = poi.description();
        return new PoiButton(font, x - SIZE / 2, y - SIZE / 2, description, poi, action);
    }

    public void tick() {
        lastFocusAnimation = focusAnimation;

        boolean focused = isHoveredOrFocused();
        if (focused && focusAnimation < HOVER_ANIMATION_LENGTH) {
            focusAnimation++;
        } else if (!focused && focusAnimation > 0) {
            focusAnimation--;
        }
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        float animation = Mth.lerp(partialTicks, lastFocusAnimation, focusAnimation) / HOVER_ANIMATION_LENGTH;
        animation = (float) (1.0 - Math.pow(1.0 - animation, 5.0));

		graphics.pose().pushPose();
        graphics.pose().translate(0.0f, 0.0f, animation > 0.0f ? SELECTED_Z_OFFSET : 0);

        if (animation > 0.0f) {
            int tooltipWidth = Mth.floor((font.width(getMessage()) + BORDER_SIZE * 2) * animation);
            final int tooltipHeight = TOOLTIP_HEIGHT;
            setWidth(SIZE + tooltipWidth);

            graphics.blitSprite(TOOLTIP_SPRITE, getX(), getY() + (getHeight() - tooltipHeight) / 2, getWidth(), tooltipHeight);
            graphics.blitSprite(TITLE_BOX_SPRITE, getX(), getY() - BORDER_SIZE, SIZE, SIZE + BORDER_SIZE * 2);

            graphics.enableScissor(getX() + BORDER_SIZE, getY() + BORDER_SIZE, getX() + getWidth() - BORDER_SIZE, getY() + getHeight() - BORDER_SIZE);
            int textLeft = getX() + SIZE + BORDER_SIZE - 1;
            int textTop = getY() + (getHeight() - font.lineHeight) / 2 + 1;
            graphics.drawString(font, getMessage(), textLeft, textTop, CommonColors.WHITE);

            graphics.disableScissor();
        } else {
            setWidth(SIZE);
        }

        int iconX = getX() + BORDER_SIZE;
        int iconY = getY() + BORDER_SIZE;

        if (animation == 0.0f) {
            RenderSystem.enableBlend();
            graphics.blitSprite(BACKGROUND_SPRITE, iconX - 1, iconY - 1, ICON_SIZE + 2, ICON_SIZE + 2);
            RenderSystem.disableBlend();
        }

        switch (poi.icon()) {
            case PoiConfig.ItemIcon(ItemStack item) -> graphics.renderFakeItem(item, iconX, iconY);
            case PoiConfig.TextureIcon(ResourceLocation texture) -> graphics.blit(texture, iconX, iconY, 0, 0.0f, 0.0f, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
        }

        List<UUID> faces = poi.faces();
        if (!faces.isEmpty()) {
            int faceFactor = faces.size() > 2 ? 2 : 1;
            for (int i = 0; i < faces.size(); i++) {
				ResourceLocation face = ClientMapManager.getFace(faces.get(i));
                PlayerFaceRenderer.draw(graphics, face, getX() + BORDER_SIZE + i * HALF_ICON_SIZE / faceFactor + i, getY() + ICON_SIZE, HALF_ICON_SIZE / faceFactor);
            }
        }

        graphics.pose().popPose();
    }

    @Override
    public void onPress() {
        action.run();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        defaultButtonNarrationText(output);
    }
}
