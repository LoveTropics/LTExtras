package com.lovetropics.extras.client.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;
import com.lovetropics.extras.client.block.state.ZiplinePoleRenderState;
import com.lovetropics.extras.zipline.ZiplineConnection;
import com.lovetropics.extras.zipline.ZiplineIndex;
import com.lovetropics.extras.zipline.ZiplinePoleBlockEntity;
import com.lovetropics.extras.zipline.ZiplineSegment;

/**
 * Draws the ropes leaving a pole as a thin square tube.
 */
public class ZiplinePoleRenderer implements BlockEntityRenderer<ZiplinePoleBlockEntity, ZiplinePoleRenderState> {
    // Placeholder: a flat vanilla texture, tinted by the vertex color
    private static final Identifier TEXTURE = Identifier.withDefaultNamespace("textures/block/white_concrete.png");
    private static final float HALF_WIDTH = 0.04f;
    private static final int COLOR = 0xFF4A4A4A;
    private static final Vector3f UP = new Vector3f(0.0f, 1.0f, 0.0f);

    public ZiplinePoleRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public ZiplinePoleRenderState createRenderState() {
        return new ZiplinePoleRenderState();
    }

    @Override
    public void extractRenderState(ZiplinePoleBlockEntity pole, ZiplinePoleRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderState.extractBase(pole, state, breakProgress);
        state.ropes.clear();
        Level level = pole.getLevel();
        if (level == null) {
            return;
        }
        ZiplineIndex index = ZiplineIndex.get(level);
        BlockPos pos = pole.getBlockPos();
        for (ZiplineConnection connection : pole.getConnections()) {
            if (index.ownsSegment(pos, connection.target())) {
                state.ropes.add(extractRope(level, new ZiplineSegment(pos, connection.target(), connection.slack())));
            }
        }
    }

    private static ZiplinePoleRenderState.Rope extractRope(Level level, ZiplineSegment segment) {
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
        return new ZiplinePoleRenderState.Rope(points, light);
    }

    @Override
    public void submit(ZiplinePoleRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        for (ZiplinePoleRenderState.Rope rope : state.ropes) {
            submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.entitySolid(TEXTURE), (pose, buffer) -> renderRope(pose, buffer, rope));
        }
    }

    private static void renderRope(PoseStack.Pose pose, VertexConsumer buffer, ZiplinePoleRenderState.Rope rope) {
        float[] points = rope.points();
        int[] light = rope.light();
        Vector3f[] corners = {new Vector3f(), new Vector3f(), new Vector3f(), new Vector3f()};
        Vector3f side = new Vector3f();
        Vector3f up = new Vector3f();
        Vector3f normal = new Vector3f();
        for (int i = 0; i + 1 < light.length; i++) {
            Vector3f start = new Vector3f(points[i * 3], points[i * 3 + 1], points[i * 3 + 2]);
            Vector3f end = new Vector3f(points[i * 3 + 3], points[i * 3 + 4], points[i * 3 + 5]);
            Vector3f direction = end.sub(start, new Vector3f()).normalize();
            direction.cross(UP, side);
            if (side.lengthSquared() < 1.0e-6f) {
                side.set(1.0f, 0.0f, 0.0f);
            }
            side.normalize(HALF_WIDTH);
            side.cross(direction, up).normalize(HALF_WIDTH);

            // Corners going around the rope, so that each pair forms an outward facing side
            up.add(side, corners[0]);
            up.sub(side, corners[1]);
            up.negate(corners[2]).sub(side);
            side.sub(up, corners[3]);

            for (int face = 0; face < 4; face++) {
                Vector3f a = corners[face];
                Vector3f b = corners[(face + 1) % 4];
                a.add(b, normal).normalize();
                vertex(buffer, pose, start, a, light[i], normal);
                vertex(buffer, pose, end, a, light[i + 1], normal);
                vertex(buffer, pose, end, b, light[i + 1], normal);
                vertex(buffer, pose, start, b, light[i], normal);
            }
        }
    }

    private static void vertex(VertexConsumer buffer, PoseStack.Pose pose, Vector3f point, Vector3f offset, int light, Vector3f normal) {
        buffer.addVertex(pose, point.x + offset.x, point.y + offset.y, point.z + offset.z)
                .setColor(COLOR)
                .setUv(0.0f, 0.0f)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, normal.x, normal.y, normal.z);
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override
    public int getViewDistance() {
        return ZiplineSegment.MAX_SPAN * 2;
    }

    @Override
    public AABB getRenderBoundingBox(ZiplinePoleBlockEntity pole) {
        return pole.getRenderBounds();
    }
}
