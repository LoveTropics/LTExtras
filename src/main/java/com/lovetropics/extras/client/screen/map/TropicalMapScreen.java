package com.lovetropics.extras.client.screen.map;

import com.lovetropics.extras.client.ClientMapPoiManager;
import com.lovetropics.extras.data.poi.MapPoiManager;
import com.lovetropics.extras.data.poi.Poi;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.tuple.Pair;

public class TropicalMapScreen extends Screen {
    private static final int ICON_SIZE = 8;
    private static final int FONT_SCALING = 2;
    private static final int MAP_PNG_HEIGHT = 256;
    private static final int MAP_PNG_WIDTH = 256;
    private static final ResourceLocation MAP_LOCATION = new ResourceLocation("ltextras", "textures/map.png");
    private final Player player;

    public TropicalMapScreen(final Component title, final Player player) {
        super(title);
        this.player = player;
    }

    @Override
    protected void init() {
        super.init();
        int xOffset = (this.width / 2) - (MAP_PNG_WIDTH / 2);
        int yOffset = (this.height / 2) - (MAP_PNG_HEIGHT / 2);

        for (Poi mapPoi : ClientMapPoiManager.getPois().values()) {
            final BlockPos pos = mapPoi.globalPos().pos();
            final Pair<Integer, Integer> poiPos = getPoiPos(pos);
            final int screenX = poiPos.getLeft() + xOffset;
            final int screenY = poiPos.getRight() + yOffset;

            addRenderableWidget(new PoiImageButton(mapPoi.name(), player.canUseGameMasterBlocks(), screenX, screenY, ICON_SIZE, ICON_SIZE, 0, 0, 0,
                    mapPoi.resourceLocation(), ICON_SIZE, ICON_SIZE,
                    b -> doWarp(mapPoi)));

            //This adds an invisible area where the text is so not just the icon is clickable
            int textWidthClickableArea = this.font.width(mapPoi.description());
            addRenderableWidget(new InvisibleButton(Button.builder(mapPoi.description(), b -> doWarp(mapPoi))
                    .size(textWidthClickableArea + ICON_SIZE, ICON_SIZE)
                    .pos(screenX, screenY)));
        }
    }

    private Pair<Integer, Integer> getPoiPos(final BlockPos blockPos) {
        final int mapWidth = MapPoiManager.MAP_BB.maxX() - MapPoiManager.MAP_BB.minX();
        final int mapHeight = MapPoiManager.MAP_BB.maxZ() - MapPoiManager.MAP_BB.minZ();
        final int screenX = (blockPos.getX() - MapPoiManager.MAP_BB.minX()) * MAP_PNG_WIDTH / mapWidth;
        final int screenY = (blockPos.getZ() - MapPoiManager.MAP_BB.minZ()) * MAP_PNG_HEIGHT / mapHeight;
        return Pair.of(screenX, screenY);
    }

    @Override
    public void render(final GuiGraphics guiGraphics, final int pMouseX, final int pMouseY, final float pPartialTick) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, pMouseX, pMouseY, pPartialTick);

        final int xOffset = (this.width / 2) - (MAP_PNG_WIDTH / 2);
        final int yOffset = (this.height / 2) - (MAP_PNG_HEIGHT / 2);

        //Dont think this is a great way to scale... just wanted the text a bit smaller :)
        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        pose.scale(1.0f / FONT_SCALING, 1.0f / FONT_SCALING, 1.0f / FONT_SCALING);
        for (Poi mapPoi : ClientMapPoiManager.getPois().values()) {
            final BlockPos pos = mapPoi.globalPos().pos();
            final Pair<Integer, Integer> poiPos = getPoiPos(pos);
            final int screenX = poiPos.getLeft() + xOffset;
            final int screenY = poiPos.getRight() + yOffset;

            Component mapComponent = mapPoi.description();
            if (!mapPoi.enabled() && !player.canUseGameMasterBlocks()) {
                continue;
            } else if (!mapPoi.enabled() && player.canUseGameMasterBlocks() && mapComponent instanceof final MutableComponent mc) {
                mapComponent = mc.copy().append(" [DISABLED]");
            }
            final TextColor textColor = mapComponent.getStyle().getColor();
            final int color = textColor == null ? 0xFFFFFF : textColor.getValue();

            guiGraphics.drawString(this.font, mapComponent, (screenX + ICON_SIZE) * FONT_SCALING,
                    (screenY + (this.font.lineHeight / 4)) * FONT_SCALING, color);

        }
        pose.popPose();
    }

    @Override
    public void renderBackground(final GuiGraphics guiGraphics) {
        int h = (this.height - MAP_PNG_HEIGHT) / 2;
        int w = (this.width - MAP_PNG_WIDTH) / 2;

        guiGraphics.blit(MAP_LOCATION, w, h, 0, 0.0F, 0.0F, MAP_PNG_WIDTH, MAP_PNG_HEIGHT, MAP_PNG_WIDTH, MAP_PNG_HEIGHT);
    }

    private void doWarp(Poi mapPoi) {
        if (player instanceof final LocalPlayer localPlayer) {
            localPlayer.connection.sendUnsignedCommand("warp " + mapPoi.name());
        }
    }
}