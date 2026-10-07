package com.lovetropics.extras.zipline;

import com.lovetropics.extras.data.attachment.ExtraAttachments;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tracks the zipline poles that are loaded in a level, so that ropes can be founde without scanning the world.
 */
public final class ZiplineIndex {
    private final Map<BlockPos, ZiplinePoleBlockEntity> poles = new HashMap<>();

    public static ZiplineIndex get(Level level) {
        return level.getData(ExtraAttachments.ZIPLINE_INDEX);
    }

    void add(ZiplinePoleBlockEntity pole) {
        poles.put(pole.getBlockPos(), pole);
    }

    void remove(ZiplinePoleBlockEntity pole) {
        poles.remove(pole.getBlockPos(), pole);
    }

    public @Nullable ZiplineSegment getSegment(BlockPos from, BlockPos to) {
        ZiplinePoleBlockEntity pole = poles.get(from);
        ZiplineConnection connection;
        if (pole != null) {
            connection = pole.getConnection(to);
        } else {
            ZiplinePoleBlockEntity otherPole = poles.get(to);
            connection = otherPole != null ? otherPole.getConnection(from) : null;
        }
        return connection != null ? new ZiplineSegment(from, to, connection.slack()) : null;
    }

    public List<ZiplineSegment> getSegmentsFrom(BlockPos pos) {
        ZiplinePoleBlockEntity pole = poles.get(pos);
        if (pole == null) {
            return List.of();
        }
        List<ZiplineSegment> segments = new ArrayList<>(pole.getConnections().size());
        for (ZiplineConnection connection : pole.getConnections()) {
            segments.add(new ZiplineSegment(pos, connection.target(), connection.slack()));
        }
        return segments;
    }

    /**
     * @return the segment leaving the given pole that is best aligned with the given direction
     */
    public @Nullable ZiplineSegment pickSegmentFrom(BlockPos pos, Vec3 direction) {
        ZiplineSegment best = null;
        double bestAlignment = Double.NEGATIVE_INFINITY;
        for (ZiplineSegment segment : getSegmentsFrom(pos)) {
            double alignment = direction.dot(segment.tangent(0.0));
            if (alignment > bestAlignment) {
                best = segment;
                bestAlignment = alignment;
            }
        }
        return best;
    }

    /**
     * @return the segment that leads on from the given pole, away from the pole that the rider arrived from
     */
    public @Nullable ZiplineSegment getContinuation(BlockPos pos, BlockPos arrivedFrom) {
        for (ZiplineSegment segment : getSegmentsFrom(pos)) {
            if (!segment.to().equals(arrivedFrom)) {
                return segment;
            }
        }
        return null;
    }

    /**
     * Both poles know about the rope between them: only consider it from one of them to avoid duplicates.
     */
    public boolean ownsSegment(BlockPos pos, BlockPos target) {
        return pos.compareTo(target) < 0 || !poles.containsKey(target);
    }

    public ZiplineSegment.@Nullable Hit raycast(Vec3 start, Vec3 end, double radius) {
        AABB rayBounds = new AABB(start, end).inflate(radius);
        ZiplineSegment.Hit closest = null;
        for (ZiplinePoleBlockEntity pole : poles.values()) {
            for (ZiplineConnection connection : pole.getConnections()) {
                if (!ownsSegment(pole.getBlockPos(), connection.target())) {
                    continue;
                }
                ZiplineSegment segment = new ZiplineSegment(pole.getBlockPos(), connection.target(), connection.slack());
                if (!segment.bounds().inflate(radius).intersects(rayBounds)) {
                    continue;
                }
                ZiplineSegment.Hit hit = segment.raycast(start, end, radius);
                if (hit != null && (closest == null || hit.distance() < closest.distance())) {
                    closest = hit;
                }
            }
        }
        return closest;
    }
}
