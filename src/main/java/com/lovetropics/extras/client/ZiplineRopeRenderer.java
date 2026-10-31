package com.lovetropics.extras.client;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.zipline.ZiplineNetwork;
import com.lovetropics.extras.zipline.ZiplineSegment;
import com.lovetropics.extras.zipline.ZiplineStyle;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ExtractLevelRenderStateEvent;
import net.neoforged.neoforge.client.event.SubmitCustomGeometryEvent;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * Draws every rope in view in its {@link ZiplineStyle}: like an old rope block, or as a stalk of bamboo. Ropes are drawn from the {@link ZiplineNetwork} rather than from their
 * poles, so that they are still there when neither of their poles is within render distance.
 */
@EventBusSubscriber(Dist.CLIENT)
public final class ZiplineRopeRenderer {
    private static final ContextKey<List<Rope>> ROPES = new ContextKey<>(LTExtras.id("zipline_ropes"));

    private static final Identifier OLD_ROPE_TEXTURE = LTExtras.id("textures/block/old_rope.png");
    private static final float OLD_ROPE_HALF_WIDTH = 0.5f;

    private static final Identifier BAMBOO_TEXTURE = Identifier.withDefaultNamespace("textures/block/bamboo_stalk.png");
    // As thick as a grown bamboo plant, using the same part of the texture for its sides
    private static final float BAMBOO_HALF_WIDTH = 1.5f / 16.0f;
    private static final float BAMBOO_SIDE_MIN_U = 0.0f;
    private static final float BAMBOO_SIDE_MAX_U = 3.0f / 16.0f;
    private static final int COLOR = 0xFFFFFFFF;
    private static final Vector3f UP = new Vector3f(0.0f, 1.0f, 0.0f);

    @SubscribeEvent
    public static void onExtractLevelRenderState(ExtractLevelRenderStateEvent event) {
        ClientLevel level = event.getLevel();
        List<ZiplineSegment> segments = ZiplineNetwork.get(level).getRopes();
        if (segments.isEmpty()) {
            return;
        }
        Vec3 cameraPosition = event.getCamera().position();
        Frustum frustum = event.getFrustum();
        double maxDistance = Minecraft.getInstance().options.getEffectiveRenderDistance() * SectionPos.SECTION_SIZE;
        List<Rope> ropes = new ArrayList<>();
        for (ZiplineSegment segment : segments) {
            AABB bounds = segment.bounds().inflate(OLD_ROPE_HALF_WIDTH);
            if (bounds.distanceToSqr(cameraPosition) <= maxDistance * maxDistance && frustum.isVisible(bounds)) {
                ropes.add(extractRope(level, segment));
            }
        }
        event.getRenderState().setRenderData(ROPES, ropes);
    }

    private static Rope extractRope(Level level, ZiplineSegment segment) {
        BlockPos origin = segment.from();
        int samples = segment.sampleCount();
        float[] points = new float[(samples + 1) * 3];
        int[] light = new int[samples + 1];
        for (int i = 0; i <= samples; i++) {
            Vec3 point = segment.point((double) i / samples);
            points[i * 3] = (float) (point.x - origin.getX());
            points[i * 3 + 1] = (float) (point.y - origin.getY());
            points[i * 3 + 2] = (float) (point.z - origin.getZ());
            BlockPos lightPos = BlockPos.containing(point);
            light[i] = LightCoordsUtil.pack(level.getBrightness(LightLayer.BLOCK, lightPos), level.getBrightness(LightLayer.SKY, lightPos));
        }
        return new Rope(origin, segment.settings().style(), points, light);
    }

    @SubscribeEvent
    public static void onSubmitCustomGeometry(SubmitCustomGeometryEvent event) {
        List<Rope> ropes = event.getLevelRenderState().getRenderData(ROPES);
        if (ropes == null) {
            return;
        }
        Vec3 cameraPosition = event.getLevelRenderState().cameraRenderState.pos;
        PoseStack poseStack = event.getPoseStack();
        for (Rope rope : ropes) {
            poseStack.pushPose();
            poseStack.translate(rope.origin().getX() - cameraPosition.x, rope.origin().getY() - cameraPosition.y, rope.origin().getZ() - cameraPosition.z);
            switch (rope.style()) {
                case OLD_ROPE -> event.getSubmitNodeCollector().submitCustomGeometry(poseStack, RenderTypes.entityCutout(OLD_ROPE_TEXTURE), (pose, buffer) -> renderOldRope(pose, buffer, rope));
                case BAMBOO -> event.getSubmitNodeCollector().submitCustomGeometry(poseStack, RenderTypes.entitySolid(BAMBOO_TEXTURE), (pose, buffer) -> renderBamboo(pose, buffer, rope));
            }
            poseStack.popPose();
        }
    }

    private static void renderOldRope(PoseStack.Pose pose, VertexConsumer buffer, Rope rope) {
        float[] points = rope.points();
        int[] light = rope.light();
        Vector3f side = new Vector3f();
        Vector3f up = new Vector3f();
        Vector3f[] offsets = {new Vector3f(), new Vector3f()};
        Vector3f[] normals = {new Vector3f(), new Vector3f()};

        float startV = 0.0f;
        for (int i = 0; i + 1 < light.length; i++) {
            Vector3f start = new Vector3f(points[i * 3], points[i * 3 + 1], points[i * 3 + 2]);
            Vector3f end = new Vector3f(points[i * 3 + 3], points[i * 3 + 4], points[i * 3 + 5]);
            float endV = startV + start.distance(end);
            Vector3f direction = end.sub(start, new Vector3f()).normalize();
            direction.cross(UP, side);
            if (side.lengthSquared() < 1.0e-6f) {
                side.set(1.0f, 0.0f, 0.0f);
            }
            side.normalize();
            side.cross(direction, up).normalize();

            // Diagonal planes
            up.add(side, offsets[0]).normalize(OLD_ROPE_HALF_WIDTH);
            up.sub(side, offsets[1]).normalize(OLD_ROPE_HALF_WIDTH);
            offsets[1].normalize(normals[0]);
            offsets[0].negate(normals[1]).normalize();

            for (int plane = 0; plane < 2; plane++) {
                Vector3f offset = offsets[plane];
                Vector3f normal = normals[plane];
                Vector3f negated = offset.negate(new Vector3f());
                vertex(buffer, pose, start, offset, 0.0f, startV, light[i], normal);
                vertex(buffer, pose, end, offset, 0.0f, endV, light[i + 1], normal);
                vertex(buffer, pose, end, negated, 1.0f, endV, light[i + 1], normal);
                vertex(buffer, pose, start, negated, 1.0f, startV, light[i], normal);
            }
            startV = endV;
        }
    }

    private static void renderBamboo(PoseStack.Pose pose, VertexConsumer buffer, Rope rope) {
        float[] points = rope.points();
        int[] light = rope.light();
        Vector3f[] corners = {new Vector3f(), new Vector3f(), new Vector3f(), new Vector3f()};
        Vector3f side = new Vector3f();
        Vector3f up = new Vector3f();
        Vector3f normal = new Vector3f();
        // The texture repeats every block along the rope, like it does up a bamboo plant
        float startV = 0.0f;
        for (int i = 0; i + 1 < light.length; i++) {
            Vector3f start = new Vector3f(points[i * 3], points[i * 3 + 1], points[i * 3 + 2]);
            Vector3f end = new Vector3f(points[i * 3 + 3], points[i * 3 + 4], points[i * 3 + 5]);
            float endV = startV + start.distance(end);
            Vector3f direction = end.sub(start, new Vector3f()).normalize();
            direction.cross(UP, side);
            if (side.lengthSquared() < 1.0e-6f) {
                side.set(1.0f, 0.0f, 0.0f);
            }
            side.normalize(BAMBOO_HALF_WIDTH);
            side.cross(direction, up).normalize(BAMBOO_HALF_WIDTH);

            // Corners going around the rope, so that each pair forms an outward facing side
            up.add(side, corners[0]);
            up.sub(side, corners[1]);
            up.negate(corners[2]).sub(side);
            side.sub(up, corners[3]);

            for (int face = 0; face < 4; face++) {
                Vector3f a = corners[face];
                Vector3f b = corners[(face + 1) % 4];
                a.add(b, normal).normalize();
                vertex(buffer, pose, start, a, BAMBOO_SIDE_MIN_U, startV, light[i], normal);
                vertex(buffer, pose, end, a, BAMBOO_SIDE_MIN_U, endV, light[i + 1], normal);
                vertex(buffer, pose, end, b, BAMBOO_SIDE_MAX_U, endV, light[i + 1], normal);
                vertex(buffer, pose, start, b, BAMBOO_SIDE_MAX_U, startV, light[i], normal);
            }
            startV = endV;
        }
    }

    private static void vertex(VertexConsumer buffer, PoseStack.Pose pose, Vector3f point, Vector3f offset, float u, float v, int light, Vector3f normal) {
        buffer.addVertex(pose, point.x + offset.x, point.y + offset.y, point.z + offset.z)
                .setColor(COLOR)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, normal.x, normal.y, normal.z);
    }

    /**
     * @param origin where the points are relative to, so that they stay precise far from the world origin
     * @param style  what the rope looks like
     * @param points xyz of each point along the rope
     * @param light  packed light at each point
     */
    private record Rope(BlockPos origin, ZiplineStyle style, float[] points, int[] light) {
    }
}
