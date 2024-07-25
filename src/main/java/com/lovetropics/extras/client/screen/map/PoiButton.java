package com.lovetropics.extras.client.screen.map;

import com.lovetropics.extras.client.ClientMapPoiManager;
import com.lovetropics.extras.data.poi.Poi;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CommonColors;
import net.minecraft.util.Mth;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

class PoiButton extends AbstractButton {
    private static final int ICON_SIZE = 16;
    private static final int HALF_ICON_SIZE = ICON_SIZE / 2;
    private static final int BORDER_SIZE = 3;
    private static final int SIZE = ICON_SIZE + BORDER_SIZE * 2;
    private static final int TOOLTIP_HEIGHT = 18;

    private static final int HOVER_ANIMATION_LENGTH = 8;

    private static final ResourceLocation TOOLTIP_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/advancements/widgets.png");

    private final Poi poi;
    private final Font font;
    private final Consumer<Poi> action;

    private int lastFocusAnimation;
    private int focusAnimation;

    private PoiButton(Font font, int x, int y, Component message, Poi poi, Consumer<Poi> action) {
        super(x, y, SIZE, SIZE, message);
        this.poi = poi;
        this.font = font;
        this.action = action;
    }

    public static PoiButton create(Font font, int x, int y, Poi poi, Consumer<Poi> action) {
        Component description = poi.description();
        if (!poi.enabled()) {
            description = Component.empty()
                    .append(Component.literal("\uD83D\uDD12 ").withStyle(ChatFormatting.RED))
                    .append(description)
                    .withStyle(ChatFormatting.GRAY);
        }
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

        int zOffset = animation > 0.0f ? 100 : 0;

        if (animation > 0.0f) {
            int tooltipWidth = Mth.floor((font.width(getMessage()) + BORDER_SIZE * 2) * animation);
            final int tooltipHeight = TOOLTIP_HEIGHT;
            setWidth(SIZE + tooltipWidth);

            graphics.pose().pushPose();
            graphics.pose().translate(0.0f, 0.0f, zOffset);

            graphics.blitSprite(TOOLTIP_LOCATION, getX(), getY() + (getHeight() - tooltipHeight) / 2, getWidth(), tooltipHeight, BORDER_SIZE, 200, 20, 0, 29);
            graphics.blitSprite(TOOLTIP_LOCATION, getX(), getY(), SIZE, SIZE, BORDER_SIZE, 200, 20, 0, 55);

            graphics.enableScissor(getX() + BORDER_SIZE, getY() + BORDER_SIZE, getX() + getWidth() - BORDER_SIZE, getY() + getHeight() - BORDER_SIZE);
            int textLeft = getX() + SIZE + BORDER_SIZE - 1;
            int textTop = getY() + (getHeight() - font.lineHeight) / 2 + 1;
            graphics.drawString(font, getMessage(), textLeft, textTop, CommonColors.WHITE);

            graphics.disableScissor();

            graphics.pose().popPose();
        } else {
            setWidth(SIZE);
        }

        ResourceLocation icon = poi.resourceLocation();
        graphics.blit(icon, getX() + BORDER_SIZE, getY() + BORDER_SIZE, zOffset, 0.0f, 0.0f, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);

        List<UUID> faces = poi.faces();
        if (!faces.isEmpty()) {
            int faceFactor = faces.size() > 2 ? 2 : 1;
            graphics.pose().pushPose();
            graphics.pose().translate(0.0f, 0.0f, zOffset);
            for (int i = 0; i < faces.size(); i++) {
                UUID uuid = faces.get(i);
                ResourceLocation face = ClientMapPoiManager.getFace(uuid);
                PlayerFaceRenderer.draw(graphics, face, getX() + BORDER_SIZE + i * HALF_ICON_SIZE / faceFactor + i, getY() + ICON_SIZE, HALF_ICON_SIZE / faceFactor);
            }
            graphics.pose().popPose();
        }
    }

    @Override
    public void onPress() {
        action.accept(poi);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        defaultButtonNarrationText(output);
    }
}
