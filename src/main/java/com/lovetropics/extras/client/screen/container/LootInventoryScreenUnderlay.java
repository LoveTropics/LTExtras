package com.lovetropics.extras.client.screen.container;

import com.lovetropics.extras.ExtraDataComponents;
import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.template.item.modifers.LootCategory;
import com.lovetropics.extras.template.item.modifers.STTLootCategoryDisplayData;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * LTExtras
 * FIRE EMOJI, FIRE EMOJI, FIRE EMOJI
 */
public final class LootInventoryScreenUnderlay {

    private static final Identifier UNDERLAY = LTExtras.id("survive_the_tide_loot_underlay");
    private static final int WIDTH = 16;
    private static final int HEIGHT = 16;

    private LootInventoryScreenUnderlay() {}

    public static void injectUnderlay(GuiGraphicsExtractor graphics, STTLootCategoryDisplayData displayData, Slot slot) {
        graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                UNDERLAY,
                slot.x,
                slot.y,
                WIDTH,
                HEIGHT,
                displayData.color()
        );
    }
}
