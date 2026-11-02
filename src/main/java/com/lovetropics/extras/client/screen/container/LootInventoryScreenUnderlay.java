package com.lovetropics.extras.client.screen.container;

import com.lovetropics.extras.LTExtras;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.Slot;

/**
 * LTExtras
 * FIRE EMOJI, FIRE EMOJI, FIRE EMOJI
 */
public final class LootInventoryScreenUnderlay {

    private static final Identifier UNDERLAY = LTExtras.id("survive_the_tide_loot_underlay");
    private static final int WIDTH = 16;
    private static final int HEIGHT = 16;

    private static final Identifier HOTBAR_UNDERLAY = LTExtras.id("survive_the_tide_loot_hotbar_underlay");
    private static final int HOTBAR_WIDTH = 16;
    private static final int HOTBAR_HEIGHT = 16;


    private LootInventoryScreenUnderlay() {}

    public static void injectUnderlay(GuiGraphicsExtractor graphics, int displayData, Slot slot) {
        graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                UNDERLAY,
                slot.x,
                slot.y,
                WIDTH,
                HEIGHT,
                displayData
        );
    }

    public static void injectHotbarUnderlay(GuiGraphicsExtractor graphics, int displayData, int x, int y) {
        graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                HOTBAR_UNDERLAY,
                x,
                y,
                HOTBAR_WIDTH,
                HOTBAR_HEIGHT,
                displayData
        );
    }
}
