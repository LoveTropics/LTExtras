package com.lovetropics.extras.client;

import com.lovetropics.extras.ExtraDataComponents;
import com.lovetropics.extras.ExtraItems;
import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.item.ImageData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.CommonColors;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

import java.util.List;

@EventBusSubscriber(Dist.CLIENT)
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
            ItemStack item = player.getMainHandItem();
            ImageData image = item.get(ExtraDataComponents.IMAGE);
            if ((item.is(ExtraItems.INVITE) || item.is(ExtraItems.QUEST)) && image != null) {
                drawImage(graphics, image);
            }
        });
    }

    private static void drawImage(GuiGraphicsExtractor graphics, ImageData image) {
        Font font = Minecraft.getInstance().font;
        int height = Math.min((int) image.height(), graphics.guiHeight() - PADDING * 2);
        float scale = height / image.height();

        int width = Mth.floor(image.width() * scale);

        int left = (graphics.guiWidth() - width) / 2;
        int top = (graphics.guiHeight() - height) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, image.texture(), left, top, 0, 0, width, height, 1, 1, 1, 1);

        graphics.pose().pushMatrix();
        graphics.pose().translate(left, top);
        graphics.pose().scale(scale, scale);

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
                graphics.text(font, line, Mth.floor(lineLeft), Mth.floor(lineTop), CommonColors.WHITE);
                lineTop += lineSpacing;
            }
        }

        graphics.pose().popMatrix();
    }
}
