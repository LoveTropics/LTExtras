package com.lovetropics.extras.client;

import com.lovetropics.extras.ExtraItems;
import com.lovetropics.extras.collectible.Collectible;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CommonColors;
import net.minecraft.world.item.ItemStack;

public class CollectibleToast implements Toast {
    private static final Component TITLE = Component.translatable("toast.collectible.title");

    private static final long VISIBILITY_TIME_MS = 1000 * 3;
    private static final int WIDTH = 160;
    private static final int HEIGHT = 32;

    private static final ResourceLocation SPRITE = ResourceLocation.withDefaultNamespace("toast/recipe");

    private final ItemStack stack;
    private final Component name;
    private final Minecraft minecraft = Minecraft.getInstance();

    public CollectibleToast(Holder<Collectible> collectible) {
        stack = Collectible.createItemStack(collectible, Util.NIL_UUID);
        name = Component.empty().withStyle(ChatFormatting.DARK_RED).append(stack.getHoverName());
    }

    @Override
    public Visibility render(GuiGraphics graphics, ToastComponent component, long time) {
        graphics.blitSprite(SPRITE, 0, 0, WIDTH, HEIGHT);

        graphics.pose().pushPose();
        graphics.pose().translate(0.0f, 0.0f, 100.0f);
        graphics.renderFakeItem(new ItemStack(ExtraItems.COLLECTIBLE_BASKET.asItem()), 11, 12);
        graphics.pose().popPose();
        graphics.renderFakeItem(stack, 5, 4);

        Font font = minecraft.font;
        final int textLeft = 30;
        graphics.drawString(font, TITLE, textLeft, 7, CommonColors.BLACK, false);
        graphics.drawString(font, Component.translatable("toast.collectible.item", name), textLeft, 18, CommonColors.BLACK, false);

        return time < VISIBILITY_TIME_MS ? Visibility.SHOW : Visibility.HIDE;
    }

    @Override
    public int width() {
        return WIDTH;
    }

    @Override
    public int height() {
        return HEIGHT;
    }
}
