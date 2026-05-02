package com.lovetropics.extras.client;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.client.keybinds.ForkliftKeybinds;
import com.lovetropics.extras.entity.ForkliftEntity;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

import java.awt.*;

@EventBusSubscriber(Dist.CLIENT)
public class ClientPlayerForkliftHUD {

    private static ForkliftDriftBarRenderer DRIFT_BAR;

    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        DRIFT_BAR = new ForkliftDriftBarRenderer(Minecraft.getInstance());
        event.registerBelow(VanillaGuiLayers.CAMERA_OVERLAYS, LTExtras.id("forklift_hud"), ClientPlayerForkliftHUD::renderGui);
    }

    private static void renderGui(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }

        final LocalPlayer player = Minecraft.getInstance().player;

        if (player != null && player.isPassenger() && player.getVehicle() instanceof ForkliftEntity forkliftEntity) {
            String raise = ForkliftKeybinds.RAISE_FORKLIFT.getTranslatedKeyMessage().getString();
            String lower = ForkliftKeybinds.LOWER_FORKLIFT.getTranslatedKeyMessage().getString();
            String drift = ForkliftKeybinds.DRIFT.getTranslatedKeyMessage().getString();
            String eject = ForkliftKeybinds.EJECT_FORK_RIDERS.getTranslatedKeyMessage().getString();
            graphics.text(Minecraft.getInstance().font, "Raise Lift: " + raise, 2, 20, Color.WHITE.getRGB());
            graphics.text(Minecraft.getInstance().font, "Lower Lift: " + lower, 2, 30, Color.WHITE.getRGB());
            graphics.text(Minecraft.getInstance().font, "Drift: " + drift, 2, 40, Color.WHITE.getRGB());
            graphics.text(Minecraft.getInstance().font, "Eject Riders: " + eject, 2, 50, Color.WHITE.getRGB());
            graphics.text(Minecraft.getInstance().font, "Is drifting: " + forkliftEntity.isDrifting(), 2, 60, Color.GREEN.getRGB());
            graphics.text(Minecraft.getInstance().font, "Drift cooldown: " + forkliftEntity.driftCooldown, 2, 70, Color.GREEN.getRGB());

            DRIFT_BAR.render(graphics, forkliftEntity);
        }
    }
}
