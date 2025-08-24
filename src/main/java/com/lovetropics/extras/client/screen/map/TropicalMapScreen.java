package com.lovetropics.extras.client.screen.map;

import com.lovetropics.extras.client.map.ClientPoi;
import com.lovetropics.extras.data.poi.MapConfig;
import com.lovetropics.extras.data.poi.MapManager;
import com.lovetropics.extras.data.poi.PoiConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class TropicalMapScreen extends Screen {
    private final Player player;
    private final List<PoiButton> poiButtons = new ArrayList<>();
    private final MapConfig map;
    private final List<ClientPoi> pois;

    public TropicalMapScreen(Player player, MapConfig map, List<ClientPoi> pois) {
        super(map.description());
        this.player = player;
		this.map = map;
		this.pois = pois;
	}

    @Override
    protected void init() {
        super.init();

        poiButtons.clear();
        int xOffset = (width / 2) - (MapManager.MAP_SIZE / 2);
        int yOffset = (height / 2) - (MapManager.MAP_SIZE / 2);

        for (ClientPoi poi : pois) {
            int screenX = poi.markerX() + xOffset;
            int screenY = poi.markerY() + yOffset;

            PoiButton button = PoiButton.create(font, screenX, screenY, poi, () -> doWarp(poi.id()));
            addRenderableWidget(button);
            poiButtons.add(button);
        }
    }

    @Override
    public void tick() {
        super.tick();
        for (PoiButton button : poiButtons) {
            button.tick();
        }
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderBackground(graphics, pMouseX, pMouseY, pPartialTick);

        int h = (height - MapManager.MAP_SIZE) / 2;
        int w = (width - MapManager.MAP_SIZE) / 2;

		graphics.blit(RenderPipelines.GUI_TEXTURED, map.texture(), w, h, 0, 0, MapManager.MAP_SIZE, MapManager.MAP_SIZE, MapManager.MAP_SIZE, MapManager.MAP_SIZE);
    }

    private void doWarp(ResourceKey<PoiConfig> id) {
        if (player instanceof LocalPlayer localPlayer) {
			localPlayer.connection.sendUnattendedCommand("warp " + id.location(), null);
            onClose();
        }
    }
}
