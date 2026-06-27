package com.lovetropics.extras.client.screen.map;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.client.map.ClientMapManager;
import com.lovetropics.extras.client.map.ClientPoi;
import com.lovetropics.extras.data.poi.PoiConfig;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.PlayerFaceExtractor;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.ItemStackTemplate;

import java.util.List;
import java.util.UUID;

class PoiButton extends AbstractButton {
    private static final int ICON_SIZE = 16;
    private static final int HALF_ICON_SIZE = ICON_SIZE / 2;
    private static final int BORDER_SIZE = 3;
    private static final int SIZE = ICON_SIZE + BORDER_SIZE * 2;
    private static final int TOOLTIP_HEIGHT = 18;

    private static final int HOVER_ANIMATION_LENGTH = 8;

    private static final Identifier TOOLTIP_SPRITE = LTExtras.id("widget/poi_tooltip");
    private static final Identifier BACKGROUND_SPRITE = LTExtras.id("widget/poi_background");
    private static final Identifier TITLE_BOX_SPRITE = Identifier.withDefaultNamespace("advancements/title_box");

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
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        float animation = Mth.lerp(partialTicks, lastFocusAnimation, focusAnimation) / HOVER_ANIMATION_LENGTH;
        animation = (float) (1.0 - Math.pow(1.0 - animation, 5.0));

        boolean isSelected = animation > Mth.EPSILON;

        if (isSelected) {
            int tooltipWidth = Mth.floor((font.width(getMessage()) + BORDER_SIZE * 2) * animation);
            final int tooltipHeight = TOOLTIP_HEIGHT;
            setWidth(SIZE + tooltipWidth);

            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, TOOLTIP_SPRITE, getX(), getY() + (getHeight() - tooltipHeight) / 2, getWidth(), tooltipHeight);
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, TITLE_BOX_SPRITE, getX(), getY() - BORDER_SIZE, SIZE, SIZE + BORDER_SIZE * 2);

            graphics.enableScissor(getX() + BORDER_SIZE, getY() + BORDER_SIZE, getX() + getWidth() - BORDER_SIZE, getY() + getHeight() - BORDER_SIZE);
            int textLeft = getX() + SIZE + BORDER_SIZE - 1;
            int textTop = getY() + (getHeight() - font.lineHeight) / 2 + 1;
            graphics.text(font, getMessage(), textLeft, textTop, CommonColors.WHITE);

            graphics.disableScissor();
        } else {
            setWidth(SIZE);
        }

        int iconX = getX() + BORDER_SIZE;
        int iconY = getY() + BORDER_SIZE;

        if (!isSelected) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND_SPRITE, iconX - 1, iconY - 1, ICON_SIZE + 2, ICON_SIZE + 2);
        }

        switch (poi.icon()) {
            case PoiConfig.ItemIcon(ItemStackTemplate item) -> graphics.fakeItem(item.create(), iconX, iconY);
            case PoiConfig.TextureIcon(Identifier texture) ->
                    graphics.blit(RenderPipelines.GUI_TEXTURED, texture, iconX, iconY, 0.0f, 0.0f, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE, CommonColors.WHITE);
        }

        List<UUID> faces = poi.faces();
        if (!faces.isEmpty()) {
            int faceFactor = faces.size() > 2 ? 2 : 1;
            for (int i = 0; i < faces.size(); i++) {
                PlayerSkin skin = ClientMapManager.getOnlinePlayerSkin(faces.get(i));
                PlayerFaceExtractor.extractRenderState(graphics, skin, getX() + BORDER_SIZE + i * HALF_ICON_SIZE / faceFactor + i, getY() + ICON_SIZE, HALF_ICON_SIZE / faceFactor);
            }
        }
    }

    @Override
    public void onPress(InputWithModifiers input) {
        action.run();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        defaultButtonNarrationText(output);
    }
}
