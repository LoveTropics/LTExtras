package com.lovetropics.extras.client;

import com.google.common.collect.MapMaker;
import com.lovetropics.extras.mixin.client.ModelPartAccess;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartNames;
import org.jspecify.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

public class ModelPartFinder {
    private static final float MAX_HAT_SIZE = 16.0f;

    private static final List<String> HEAD_PART_NAMES = List.of(PartNames.HEAD, "head_base", "neck", PartNames.BODY, "body_base");

    private static final ConcurrentMap<ModelPart, Optional<HeadPose>> HEAD_POSES = new MapMaker().weakKeys().makeMap();

    public static @Nullable HeadPose headPose(ModelPart root) {
        return HEAD_POSES.computeIfAbsent(root, ModelPartFinder::computeHeadPose).orElse(null);
    }

    private static Optional<HeadPose> computeHeadPose(ModelPart root) {
        List<ModelPart> path = findPathToPart(root, HEAD_PART_NAMES);
        if (path == null) {
            return Optional.empty();
        }
        List<ModelPart.Cube> cubes = ((ModelPartAccess) (Object) path.getLast()).lt$cubes();
        // It's probably fine, just pick the tallest one
        ModelPart.Cube cube = cubes.stream().max(Comparator.comparingDouble(c -> c.maxY - c.minY)).orElse(null);
        if (cube == null) {
            return Optional.empty();
        }

        float scale = Math.min(Math.min(cube.maxX - cube.minX, cube.maxZ - cube.minZ), MAX_HAT_SIZE) / 16.0f;
        float offsetZ = (cube.minZ + cube.maxZ) / 2.0f;
        return Optional.of(new HeadPose(path, scale, cube.minY / 16.0f, offsetZ / 16.0f));
    }

    /// @param candidates Part names to consider a head, in priority order
    private static @Nullable List<ModelPart> findPathToPart(ModelPart root, List<String> candidates) {
        Deque<ModelPart> stack = new ArrayDeque<>();
        stack.addLast(root);
        PartResult result = findPart(root, candidates, stack);
        return result != null ? result.parts() : null;
    }

    private static @Nullable PartResult findPart(ModelPart root, List<String> candidates, Deque<ModelPart> stack) {
        // Kind of weird to not just do a lookup into the map, but most of the time we're not going to find the part we're looking for and traverse anyway
        PartResult result = null;
        for (Map.Entry<String, ModelPart> entry : children(root).entrySet()) {
            String name = entry.getKey();
            ModelPart child = entry.getValue();
            stack.addLast(child);
            int candidateIndex = candidates.indexOf(name);
            if (candidateIndex != -1 && (result == null || candidateIndex < result.index)) {
                result = new PartResult(candidateIndex, List.copyOf(stack));
            }
            PartResult foundPart = findPart(child, candidates, stack);
            if (foundPart != null && (result == null || foundPart.index < result.index)) {
                result = foundPart;
            }
            stack.removeLast();
        }
        return result;
    }

    private static Map<String, ModelPart> children(ModelPart root) {
        return ((ModelPartAccess) (Object) root).lt$children();
    }

    private record PartResult(int index, List<ModelPart> parts) {
    }

    public record HeadPose(
            List<ModelPart> path,
            float scale,
            float offsetY,
            float offsetZ
    ) {
        public void apply(PoseStack poseStack) {
            for (ModelPart part : path) {
                part.translateAndRotate(poseStack);
            }
            poseStack.translate(0.0f, offsetY, offsetZ);
            poseStack.scale(scale, -scale, -scale);
        }
    }
}
