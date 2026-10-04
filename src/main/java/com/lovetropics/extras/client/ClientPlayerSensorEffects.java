package com.lovetropics.extras.client;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.item.sensor.PlayerSensor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.client.renderstate.AvatarRenderStateModifier;
import net.neoforged.neoforge.client.renderstate.RegisterRenderStateModifiersEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@EventBusSubscriber(Dist.CLIENT)
public class ClientPlayerSensorEffects {
    private static final Identifier MARKER_BOX_SPRITE = LTExtras.id("marker_box");
    private static final int MARKER_BOX_INNER_PADDING = 32;

    private static final ContextKey<UUID> UUID_KEY = new ContextKey<>(LTExtras.id("uuid"));

    private static final int VISIBLE_REFRESH_INTERVAL = 10;

    private static final Map<UUID, PlayerSensor.Appearance> MARKED_PLAYERS = new Object2ObjectOpenHashMap<>();
    private static final Set<UUID> VISIBLE_MARKED_PLAYERS = new ObjectArraySet<>();

    private static final List<CapturedPose> CAPTURED_SCREEN_POSE = new ArrayList<>();
    private static Matrix4f capturedProjectionMatrix = new Matrix4f();

    private static final float HEAD_SIZE = 8.0f;
    private static final float MIN_HEAD_Y = -HEAD_SIZE;
    private static final float MIN_HEAD_XZ = -HEAD_SIZE / 2.0f;
    private static final float MAX_HEAD_Y = 0.0f;
    private static final float MAX_HEAD_XZ = HEAD_SIZE / 2.0f;

    public static void mark(int entityId, PlayerSensor.Appearance appearance) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level != null && level.getEntity(entityId) instanceof Player player) {
            MARKED_PLAYERS.put(player.getUUID(), appearance);
        }
    }

    public static void clear(int entityId) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level != null && level.getEntity(entityId) instanceof Player player) {
            MARKED_PLAYERS.remove(player.getUUID());
        }
    }

    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerBelow(VanillaGuiLayers.CAMERA_OVERLAYS, LTExtras.id("player_sensor"), ClientPlayerSensorEffects::renderGui);
    }

    private static void renderGui(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }

        for (CapturedPose capturedScreenBoxes : CAPTURED_SCREEN_POSE) {
            UUID playerId = capturedScreenBoxes.playerId;
            Player target = level.getPlayerByUUID(playerId);
            PlayerSensor.Appearance appearance = MARKED_PLAYERS.get(playerId);
            if (target == null || appearance == null) {
                continue;
            }
            renderGuiMarker(graphics, capturedScreenBoxes, appearance);
        }

        CAPTURED_SCREEN_POSE.clear();
    }

    private static void renderGuiMarker(GuiGraphicsExtractor graphics, CapturedPose capturedPose, PlayerSensor.Appearance appearance) {
        ScreenBox faceBox = toScreenBox(capturedPose, MIN_HEAD_XZ, MIN_HEAD_Y, MIN_HEAD_XZ, MAX_HEAD_XZ, MAX_HEAD_Y, MAX_HEAD_XZ);
        if (faceBox == null) {
            return;
        }

        GuiBox face = faceBox.toGui(graphics);
        int faceSize = Math.max(face.width(), face.height());

        float alpha = Mth.clampedMap(faceSize, 10, 20, 0.0f, 1.0f);
        if (alpha <= Mth.EPSILON) {
            return;
        }

        int faceBoxSize = faceSize + MARKER_BOX_INNER_PADDING;
        if (appearance.markerBoxColor().isPresent()) {
            int markerColor = ARGB.color(alpha, appearance.markerBoxColor().get());
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, MARKER_BOX_SPRITE, face.centerX() - faceBoxSize / 2, face.centerY() - faceBoxSize / 2, faceBoxSize, faceBoxSize, markerColor);
        }

        for (PlayerSensor.Decoration decoration : appearance.decorations()) {
            int scale = decoration.fixedSize() ? 1 : faceBoxSize;
            int spriteWidth = Math.round(decoration.size().x * scale);
            int spriteHeight = Math.round(decoration.size().y * scale);
            int spriteOffsetX = Math.round(decoration.offset().x * scale);
            int spriteOffsetY = Math.round(decoration.offset().y * scale);
            int color = ARGB.multiplyAlpha(decoration.color(), alpha);

            if (decoration.faceAnchor().isPresent()) {
                Vector3f directionToFace = capturedPose.toWorldMatrix.transformPosition(new Vector3f(0.0f, 0.0f, 0.0f)).normalize();
                Vector3f faceForward = capturedPose.toWorldMatrix.transformDirection(new Vector3f(0.0f, 0.0f, 1.0f));
                if (faceForward.dot(directionToFace) < 0.15f) {
                    continue;
                }
                Vec2 anchor = decoration.faceAnchor().get();
                Vector3f anchorScreen = toScreenPos(capturedPose, Mth.lerp(anchor.x, MIN_HEAD_XZ, MAX_HEAD_XZ), Mth.lerp(anchor.y, MIN_HEAD_Y, MAX_HEAD_Y), MIN_HEAD_XZ + 0.5f);
                if (isVertexOffScreen(anchorScreen)) {
                    continue;
                }
                int anchorX = Mth.floor(anchorScreen.x() * graphics.guiWidth());
                int anchorY = Mth.floor(anchorScreen.y() * graphics.guiHeight());
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, decoration.sprite(), anchorX + spriteOffsetX, anchorY + spriteOffsetY, spriteWidth, spriteHeight, color);
            } else {
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, decoration.sprite(), face.centerX() + spriteOffsetX, face.centerY() - faceBoxSize / 2 + spriteOffsetY, spriteWidth, spriteHeight, color);
            }
        }
    }

    @SubscribeEvent
    public static void onStopTracking(EntityLeaveLevelEvent event) {
        if (event.getLevel() instanceof ClientLevel) {
            MARKED_PLAYERS.remove(event.getEntity().getUUID());
        }
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        LocalPlayer player = Minecraft.getInstance().player;
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

    @SubscribeEvent
    public static void onRegisterRenderStateModifiers(RegisterRenderStateModifiersEvent event) {
        event.registerAvatarEntityModifier(new AvatarRenderStateModifier() {
            @Override
            public <T extends Avatar & ClientAvatarEntity> void accept(T avatar, AvatarRenderState renderState) {
                renderState.setRenderData(UUID_KEY, avatar.getUUID());
            }
        });
    }

    public static <T extends LivingEntityRenderState> void captureModelPose(T state, EntityModel<?> model, PoseStack poseStack) {
        if (state.entityType != EntityTypes.PLAYER) {
            return;
        }
        UUID playerId = state.getRenderData(UUID_KEY);
        if (playerId == null || !VISIBLE_MARKED_PLAYERS.contains(playerId)) {
            return;
        }
        if (model instanceof HumanoidModel<?> humanoidModel) {
            CAPTURED_SCREEN_POSE.add(capturePlayerPose(playerId, poseStack, humanoidModel));
        }
    }

    private static CapturedPose capturePlayerPose(UUID entityId, PoseStack poseStack, HumanoidModel<?> humanoidModel) {
        poseStack.pushPose();
        humanoidModel.head.translateAndRotate(poseStack);
        Matrix4f toWorldMatrix = new Matrix4f(poseStack.last().pose());
        Matrix4f toScreenMatrix = RenderSystem.getModelViewMatrixCopy().mul(poseStack.last().pose());
        poseStack.popPose();
        return new CapturedPose(entityId, toWorldMatrix, toScreenMatrix);
    }

    private static @Nullable ScreenBox toScreenBox(CapturedPose pose, float x0, float y0, float z0, float x1, float y1, float z1) {
        Vector3f[] vertices = {
                toScreenPos(pose, x0, y0, z0),
                toScreenPos(pose, x0, y0, z1),
                toScreenPos(pose, x0, y1, z0),
                toScreenPos(pose, x0, y1, z1),
                toScreenPos(pose, x1, y0, z0),
                toScreenPos(pose, x1, y0, z1),
                toScreenPos(pose, x1, y1, z0),
                toScreenPos(pose, x1, y1, z1)
        };
        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float maxX = -Float.MAX_VALUE;
        float maxY = -Float.MAX_VALUE;
        for (Vector3f vertex : vertices) {
            if (isVertexOffScreen(vertex)) {
                return null;
            }
            minX = Math.min(minX, vertex.x);
            minY = Math.min(minY, vertex.y);
            maxX = Math.max(maxX, vertex.x);
            maxY = Math.max(maxY, vertex.y);
        }
        return new ScreenBox(minX, minY, maxX, maxY);
    }

    private static boolean isVertexOffScreen(Vector3f vertex) {
        boolean zZeroToOne = RenderSystem.getDevice().getDeviceInfo().isZZeroToOne();
        // Fully behind the camera
        if (vertex.z < (zZeroToOne ? 0.0f : -1.0f)) {
            return true;
        } else if (vertex.x <= -2.0f || vertex.x >= 2.0f || vertex.y <= -2.0f || vertex.y >= 2.0f) {
            return true;
        }
        return false;
    }

    public static void captureProjectionMatrix(Matrix4f projectionMatrix) {
        capturedProjectionMatrix = projectionMatrix;
    }

    private static Vector3f toScreenPos(CapturedPose pose, float x, float y, float z) {
        Vector3f pos = new Vector3f(x, y, z).mul(1.0f / 16.0f);
        pose.toScreenMatrix.transformPosition(pos);
        capturedProjectionMatrix.transformProject(pos);
        return pos.set((pos.x + 1.0f) / 2.0f, 1.0f - (pos.y + 1.0f) / 2.0f, pos.z);
    }

    private record CapturedPose(
            UUID playerId,
            Matrix4fc toWorldMatrix,
            Matrix4fc toScreenMatrix
    ) {
    }

    private record ScreenBox(float x0, float y0, float x1, float y1) {
        public GuiBox toGui(GuiGraphicsExtractor graphics) {
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
