package com.lovetropics.extras.client;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.item.sensor.PlayerSensor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@EventBusSubscriber(modid = LTExtras.MODID, value = Dist.CLIENT)
public class ClientPlayerSensorEffects {
	private static final ResourceLocation MARKER_BOX_SPRITE = LTExtras.location("marker_box");
	private static final int MARKER_BOX_INNER_PADDING = 32;

	private static final Minecraft CLIENT = Minecraft.getInstance();

	private static final int VISIBLE_REFRESH_INTERVAL = 10;

	private static final Map<UUID, PlayerSensor.Appearance> MARKED_PLAYERS = new Object2ObjectOpenHashMap<>();
	private static final Set<UUID> VISIBLE_MARKED_PLAYERS = new ObjectArraySet<>();

	private static final List<CapturedScreenBoxes> CAPTURED_SCREEN_POS = new ArrayList<>();

	public static void mark(int entityId, PlayerSensor.Appearance appearance) {
		ClientLevel level = CLIENT.level;
		if (level != null && level.getEntity(entityId) instanceof Player player) {
			MARKED_PLAYERS.put(player.getUUID(), appearance);
		}
	}

	public static void clear(int entityId) {
		ClientLevel level = CLIENT.level;
		if (level != null && level.getEntity(entityId) instanceof Player player) {
			MARKED_PLAYERS.remove(player.getUUID());
		}
	}

	public static void registerGuiLayers(RegisterGuiLayersEvent event) {
		event.registerBelow(VanillaGuiLayers.CAMERA_OVERLAYS, LTExtras.location("player_sensor"), ClientPlayerSensorEffects::renderGui);
	}

	private static void renderGui(GuiGraphics graphics, DeltaTracker deltaTracker) {
		ClientLevel level = CLIENT.level;
		if (level == null) {
			return;
		}

		for (CapturedScreenBoxes capturedScreenBoxes : CAPTURED_SCREEN_POS) {
			UUID playerId = capturedScreenBoxes.playerId;
			Player target = level.getPlayerByUUID(playerId);
			PlayerSensor.Appearance appearance = MARKED_PLAYERS.get(playerId);
			if (target == null || appearance == null) {
				continue;
			}
			renderGuiMarker(graphics, capturedScreenBoxes, appearance);
		}

		CAPTURED_SCREEN_POS.clear();
	}

	private static void renderGuiMarker(GuiGraphics graphics, CapturedScreenBoxes screenBoxes, PlayerSensor.Appearance appearance) {
		GuiBox face = screenBoxes.face.toGui(graphics);
		int faceSize = Math.max(face.width(), face.height());

		float alpha = Mth.clampedMap(faceSize, 10, 20, 0.0f, 1.0f);
		if (alpha <= Mth.EPSILON) {
			return;
		}

		int faceBoxSize = faceSize + MARKER_BOX_INNER_PADDING;
		RenderSystem.enableBlend();

		setColor(graphics, appearance.color(), alpha);
		graphics.blitSprite(MARKER_BOX_SPRITE, face.centerX() - faceBoxSize / 2, face.centerY() - faceBoxSize / 2, faceBoxSize, faceBoxSize);

		Optional<PlayerSensor.Sprite> faceSprite = appearance.faceDecoration();
		if (faceSprite.isPresent()) {
			int spriteWidth = faceSprite.get().width();
			int spriteHeight = faceSprite.get().height();
			graphics.setColor(1.0f, 1.0f, 1.0f, alpha);
			graphics.blitSprite(faceSprite.get().location(), face.centerX() - spriteWidth / 2, face.centerY() - faceBoxSize / 2 - spriteHeight, spriteWidth, spriteHeight);
		}

		graphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);

		RenderSystem.disableBlend();
	}

	private static void setColor(GuiGraphics graphics, int color, float alpha) {
		graphics.setColor(
				FastColor.ARGB32.red(color) / 255.0f,
				FastColor.ARGB32.green(color) / 255.0f,
				FastColor.ARGB32.blue(color) / 255.0f,
				alpha
		);
	}

	@SubscribeEvent
	public static void onStopTracking(EntityLeaveLevelEvent event) {
		if (event.getLevel() instanceof ClientLevel) {
			MARKED_PLAYERS.remove(event.getEntity().getUUID());
		}
	}

	@SubscribeEvent
	public static void onClientTick(ClientTickEvent.Post event) {
		LocalPlayer player = CLIENT.player;
		if (player == null) {
			return;
		}
		if (player.tickCount % VISIBLE_REFRESH_INTERVAL == 0) {
			refreshVisiblePlayers(player);
		}
	}

	private static void refreshVisiblePlayers(LocalPlayer player) {
		VISIBLE_MARKED_PLAYERS.clear();
		Level level = player.level();
		for (UUID playerId : MARKED_PLAYERS.keySet()) {
			Player target = level.getPlayerByUUID(playerId);
			if (target != null && player.hasLineOfSight(target)) {
				VISIBLE_MARKED_PLAYERS.add(target.getUUID());
			}
		}
	}

	public static <T extends LivingEntity> void captureModelPose(T entity, EntityModel<T> model, PoseStack poseStack) {
		if (entity.getType() != EntityType.PLAYER || !VISIBLE_MARKED_PLAYERS.contains(entity.getUUID())) {
			return;
		}
		if (model instanceof HumanoidModel<T> humanoidModel) {
			CapturedScreenBoxes capture = capturePlayerPose(entity, poseStack, humanoidModel);
			if (capture != null) {
				CAPTURED_SCREEN_POS.add(capture);
			}
		}
	}

	@Nullable
	private static CapturedScreenBoxes capturePlayerPose(LivingEntity entity, PoseStack poseStack, HumanoidModel<?> humanoidModel) {
		poseStack.pushPose();
		humanoidModel.head.translateAndRotate(poseStack);
		ScreenBox faceBox = toScreenBox(poseStack, -4.0f, -8.0f, -4.0f, 4.0f, 0.0f, 4.0f);
		poseStack.popPose();
		if (faceBox != null) {
			return new CapturedScreenBoxes(entity.getUUID(), faceBox);
		}
		return null;
	}

	@Nullable
	private static ScreenBox toScreenBox(PoseStack poseStack, float x0, float y0, float z0, float x1, float y1, float z1) {
		Vector3f[] vertices = {
				toScreenPos(poseStack, x0, y0, z0),
				toScreenPos(poseStack, x0, y0, z1),
				toScreenPos(poseStack, x0, y1, z0),
				toScreenPos(poseStack, x0, y1, z1),
				toScreenPos(poseStack, x1, y0, z0),
				toScreenPos(poseStack, x1, y0, z1),
				toScreenPos(poseStack, x1, y1, z0),
				toScreenPos(poseStack, x1, y1, z1)
		};
		float minX = Float.MAX_VALUE;
		float minY = Float.MAX_VALUE;
		float maxX = -Float.MAX_VALUE;
		float maxY = -Float.MAX_VALUE;
		for (Vector3f vertex : vertices) {
			if (vertex.z >= 1.0f || vertex.z <= 0.1f) {
				return null;
			} else if (vertex.x <= -2.0f || vertex.x >= 2.0f || vertex.y <= -2.0f || vertex.y >= 2.0f) {
				return null;
			}
			minX = Math.min(minX, vertex.x);
			minY = Math.min(minY, vertex.y);
			maxX = Math.max(maxX, vertex.x);
			maxY = Math.max(maxY, vertex.y);
		}
		return new ScreenBox(minX, minY, maxX, maxY);
	}

	private static Vector3f toScreenPos(PoseStack poseStack, float x, float y, float z) {
		Vector3f pos = new Vector3f(x, y, z).mul(1.0f / 16.0f);
		poseStack.last().pose().transformPosition(pos);
		RenderSystem.getModelViewMatrix().transformPosition(pos);
		RenderSystem.getProjectionMatrix().transformProject(pos);
		return pos.set((pos.x + 1.0f) / 2.0f, 1.0f - (pos.y + 1.0f) / 2.0f, pos.z);
	}

	private record CapturedScreenBoxes(UUID playerId, ScreenBox face) {
	}

	private record ScreenBox(float x0, float y0, float x1, float y1) {
		public GuiBox toGui(GuiGraphics graphics) {
			return new GuiBox(
					Mth.floor(x0 * graphics.guiWidth()), Mth.floor(y0 * graphics.guiHeight()),
					Mth.floor(x1 * graphics.guiWidth()), Mth.floor(y1 * graphics.guiHeight())
			);
		}
	}

	private record GuiBox(int x0, int y0, int x1, int y1) {
		public int centerX() {
			return (x0 + x1) / 2;
		}

		public int centerY() {
			return (y0 + y1) / 2;
		}

		public int width() {
			return x1 - x0;
		}

		public int height() {
			return y1 - y0;
		}
	}
}
