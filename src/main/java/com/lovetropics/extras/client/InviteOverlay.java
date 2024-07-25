package com.lovetropics.extras.client;

import com.lovetropics.extras.ExtraDataComponents;
import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.item.ImageData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.CommonColors;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

import java.util.List;

@EventBusSubscriber(modid = LTExtras.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class InviteOverlay {
    private static final int PADDING = 10;

    @SubscribeEvent
    public static void onRegisterOverlays(RegisterGuiLayersEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        event.registerAbove(VanillaGuiLayers.CHAT, LTExtras.location("invites"), (graphics, deltaTracker) -> {
            LocalPlayer player = minecraft.player;
            if (player == null) {
                return;
            }
            ImageData image = player.getMainHandItem().get(ExtraDataComponents.IMAGE);
            if (image != null) {
                drawImage(graphics, image);
            }
        });
    }

    private static void drawImage(GuiGraphics graphics, ImageData image) {
        Font font = Minecraft.getInstance().font;
        int height = Math.min((int) image.height(), graphics.guiHeight() - PADDING * 2);
        float scale = height / image.height();

        int width = Mth.floor(image.width() * scale);

        int left = (graphics.guiWidth() - width) / 2;
        int top = (graphics.guiHeight() - height) / 2;
        graphics.blit(image.texture(), left, top, width, height, 0, 0, 1, 1, 1, 1);

        graphics.pose().pushPose();
        graphics.pose().translate(left, top, 200.0f);
        graphics.pose().scale(scale, scale, scale);

        for (ImageData.TextElement text : image.text()) {
            int maxWidth = text.maxWidth() != Float.MAX_VALUE ? Mth.floor(text.maxWidth()) : Integer.MAX_VALUE;
            List<FormattedCharSequence> lines = font.split(text.text(), maxWidth);

            float textWidth = 0.0f;
            for (FormattedCharSequence line : lines) {
                textWidth = Math.max(textWidth, font.width(line));
            }

            float lineSpacing = text.lineSpacing();
            float textHeight = lines.size() * lineSpacing;

            float lineTop = text.alignVertical().resolve(text.y(), textHeight);
            for (FormattedCharSequence line : lines) {
                float lineLeft = text.alignHorizontal().resolve(text.x(), font.width(line));
                graphics.drawString(font, line, Mth.floor(lineLeft), Mth.floor(lineTop), CommonColors.WHITE);
                lineTop += lineSpacing;
            }
        }

        graphics.pose().popPose();
    }
}
