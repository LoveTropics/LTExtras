package com.lovetropics.extras.client;

import com.lovetropics.extras.entity.ForkliftEntity;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class ForkliftDriftBarRenderer {
    private static final ResourceLocation JUMP_BAR_BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("hud/jump_bar_background");
    private static final ResourceLocation JUMP_BAR_COOLDOWN_SPRITE = ResourceLocation.withDefaultNamespace("hud/jump_bar_cooldown");
    private static final ResourceLocation JUMP_BAR_PROGRESS_SPRITE = ResourceLocation.withDefaultNamespace("hud/jump_bar_progress");
    private final Minecraft minecraft;

    public ForkliftDriftBarRenderer(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    private int left(Window window) {
        return (window.getGuiScaledWidth() - 182) / 2;
    }

    private int top(Window window) {
        return window.getGuiScaledHeight() - 24 - 5;
    }

    public void render(GuiGraphics guiGraphics, ForkliftEntity forklift) {
        int i = left(minecraft.getWindow());
        int j = top(minecraft.getWindow());
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, JUMP_BAR_BACKGROUND_SPRITE, i, j, 182, 5);
        if (forklift.driftBuildTicks > 0) {
            int progressWidth = Mth.floor((float)forklift.driftBuildTicks / ForkliftEntity.DRIFT_TICKS * 182.0f);
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, JUMP_BAR_PROGRESS_SPRITE, 182, 5, 0, 0, i, j, progressWidth, 5);
        }
        else if (forklift.driftCooldown > 0 || forklift.driftDuration > 0) {
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, JUMP_BAR_COOLDOWN_SPRITE, i, j, 182, 5);
        }
    }
}
