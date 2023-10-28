package com.lovetropics.extras.client.screen.map;

import com.lovetropics.extras.data.poi.Poi;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CommonColors;
import net.minecraft.util.Mth;

import java.util.function.Consumer;

class PoiButton extends AbstractButton {
    private static final int ICON_SIZE = 16;
    private static final int BORDER_SIZE = 3;
    private static final int SIZE = ICON_SIZE + BORDER_SIZE * 2;
    private static final int TOOLTIP_HEIGHT = 18;

    private static final int HOVER_ANIMATION_LENGTH = 8;

    private static final ResourceLocation TOOLTIP_LOCATION = new ResourceLocation("textures/gui/advancements/widgets.png");

    private final Poi poi;
    private final Font font;
    private final Consumer<Poi> action;

    private int lastFocusAnimation;
    private int focusAnimation;

    private PoiButton(final Font font, final int x, final int y, final Component message, final Poi poi, final Consumer<Poi> action) {
        super(x, y, SIZE, SIZE, message);
        this.poi = poi;
        this.font = font;
        this.action = action;
    }

    public static PoiButton create(final Font font, final int x, final int y, final Poi poi, final Consumer<Poi> action) {
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

        final boolean focused = isHoveredOrFocused();
        if (focused && focusAnimation < HOVER_ANIMATION_LENGTH) {
            focusAnimation++;
        } else if (!focused && focusAnimation > 0) {
            focusAnimation--;
        }
    }

    @Override
    protected void renderWidget(final GuiGraphics graphics, final int mouseX, final int mouseY, final float partialTicks) {
        float animation = Mth.lerp(partialTicks, lastFocusAnimation, focusAnimation) / HOVER_ANIMATION_LENGTH;
        animation = (float) (1.0 - Math.pow(1.0 - animation, 5.0));

        if (animation > 0.0f) {
            final int tooltipWidth = Mth.floor((font.width(getMessage()) + BORDER_SIZE * 2) * animation);
            final int tooltipHeight = TOOLTIP_HEIGHT;
            setWidth(SIZE + tooltipWidth);

            graphics.pose().pushPose();
            graphics.pose().translate(0.0f, 0.0f, 100.0f);

            graphics.blitNineSliced(TOOLTIP_LOCATION, getX(), getY() + (getHeight() - tooltipHeight) / 2, getWidth(), tooltipHeight, BORDER_SIZE, 200, 20, 0, 29);
            graphics.blitNineSliced(TOOLTIP_LOCATION, getX(), getY(), SIZE, SIZE, BORDER_SIZE, 200, 20, 0, 55);

            graphics.enableScissor(getX() + BORDER_SIZE, getY() + BORDER_SIZE, getX() + getWidth() - BORDER_SIZE, getY() + getHeight() - BORDER_SIZE);
            final int textLeft = getX() + SIZE + BORDER_SIZE - 1;
            final int textTop = getY() + (getHeight() - font.lineHeight) / 2 + 1;
            graphics.drawString(font, getMessage(), textLeft, textTop, CommonColors.WHITE);

            graphics.disableScissor();

            graphics.pose().popPose();
        } else {
            setWidth(SIZE);
        }

        final ResourceLocation icon = poi.resourceLocation();
        graphics.blit(icon, getX() + BORDER_SIZE, getY() + BORDER_SIZE, 0, 0.0f, 0.0f, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
    }

    @Override
    public void onPress() {
        action.accept(poi);
    }

    @Override
    protected void updateWidgetNarration(final NarrationElementOutput output) {
        defaultButtonNarrationText(output);
    }
}
