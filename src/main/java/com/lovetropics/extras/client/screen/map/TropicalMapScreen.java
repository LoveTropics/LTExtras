package com.lovetropics.extras.client.screen.map;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.client.ClientMapPoiManager;
import com.lovetropics.extras.data.poi.MapPoiManager;
import com.lovetropics.extras.data.poi.Poi;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class TropicalMapScreen extends Screen {
    private static final int MAP_PNG_HEIGHT = 256;
    private static final int MAP_PNG_WIDTH = 256;
    private static final ResourceLocation MAP_LOCATION = LTExtras.location("textures/map.png");
    private final Player player;
    private final List<PoiButton> poiButtons = new ArrayList<>();

    public TropicalMapScreen(Component title, Player player) {
        super(title);
        this.player = player;
    }

    @Override
    protected void init() {
        super.init();

        poiButtons.clear();
        int xOffset = (width / 2) - (MAP_PNG_WIDTH / 2);
        int yOffset = (height / 2) - (MAP_PNG_HEIGHT / 2);

        for (Poi mapPoi : ClientMapPoiManager.getPois().values()) {
            if (!mapPoi.enabled() && !player.hasPermissions(Commands.LEVEL_GAMEMASTERS)) {
                continue;
            }

            BlockPos pos = mapPoi.globalPos().pos();
            Pair<Integer, Integer> poiPos = getPoiPos(pos);
            int screenX = poiPos.getLeft() + xOffset;
            int screenY = poiPos.getRight() + yOffset;

            PoiButton button = PoiButton.create(font, screenX, screenY, mapPoi, this::doWarp);
            addRenderableWidget(button);
            poiButtons.add(button);
        }
    }

    private Pair<Integer, Integer> getPoiPos(BlockPos blockPos) {
        int mapWidth = MapPoiManager.MAP_BB.maxX() - MapPoiManager.MAP_BB.minX();
        int mapHeight = MapPoiManager.MAP_BB.maxZ() - MapPoiManager.MAP_BB.minZ();
        int screenX = (blockPos.getX() - MapPoiManager.MAP_BB.minX()) * MAP_PNG_WIDTH / mapWidth;
        int screenY = (blockPos.getZ() - MapPoiManager.MAP_BB.minZ()) * MAP_PNG_HEIGHT / mapHeight;
        return Pair.of(screenX, screenY);
    }

    @Override
    public void tick() {
        super.tick();
        for (PoiButton button : poiButtons) {
            button.tick();
        }
    }

    @Override
    public void render(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(graphics, pMouseX, pMouseY, pPartialTick);
        super.render(graphics, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderBackground(graphics, pMouseX, pMouseY, pPartialTick);

        int h = (height - MAP_PNG_HEIGHT) / 2;
        int w = (width - MAP_PNG_WIDTH) / 2;

        graphics.blit(MAP_LOCATION, w, h, 0, 0.0F, 0.0F, MAP_PNG_WIDTH, MAP_PNG_HEIGHT, MAP_PNG_WIDTH, MAP_PNG_HEIGHT);
    }

    private void doWarp(Poi mapPoi) {
        if (player instanceof LocalPlayer localPlayer) {
            localPlayer.connection.sendUnsignedCommand("warp " + mapPoi.name());
            onClose();
        }
    }
}
