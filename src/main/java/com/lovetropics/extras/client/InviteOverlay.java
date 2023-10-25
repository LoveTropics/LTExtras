package com.lovetropics.extras.client;

import com.lovetropics.extras.ExtraItems;
import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.item.ImageData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = LTExtras.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class InviteOverlay {
    private static final int PADDING = 10;

    @SubscribeEvent
    public static void onRegisterOverlays(final RegisterGuiOverlaysEvent event) {
        final Minecraft minecraft = Minecraft.getInstance();
        event.registerAbove(VanillaGuiOverlay.SUBTITLES.id(), "invites", (gui, graphics, partialTick, screenWidth, screenHeight) -> {
            final LocalPlayer player = minecraft.player;
            if (player == null) {
                return;
            }
            final ItemStack item = player.getMainHandItem();
            if (item.is(ExtraItems.INVITE.get())) {
                ImageData.get(item).ifPresent(image -> drawImage(graphics, screenWidth, screenHeight, image));
            }
        });
    }

    private static void drawImage(final GuiGraphics graphics, final int screenWidth, final int screenHeight, final ImageData image) {
        final int height = Math.min((int) image.height(), screenHeight - PADDING * 2);
        final int width = Mth.floor(height * image.width() / image.height());

        final int left = (screenWidth - width) / 2;
        final int top = (screenHeight - height) / 2;
        graphics.blit(image.texture(), left, top, width, height, 0, 0, 1, 1, 1, 1);
    }
}
