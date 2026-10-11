package com.lovetropics.extras.zipline;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class ZiplineSafety {
    private static final double CONTACT_GAP = 1.0E-5;

    private ZiplineSafety() {
    }

    private static AABB riderBounds(LivingEntity rider, Vec3 feet) {
        return rider.getDimensions(Pose.STANDING).makeBoundingBox(feet);
    }

    private static boolean withinLoadedWorld(Level level, AABB bounds) {
        if (!level.getWorldBorder().isWithinBounds(bounds)) {
            return false;
        }
        for (BlockPos pos : BlockPos.betweenClosed(BlockPos.containing(bounds.minX, bounds.minY, bounds.minZ),
                BlockPos.containing(bounds.maxX, bounds.maxY, bounds.maxZ))) {
            if (!level.isInWorldBounds(pos) || !level.hasChunkAt(pos)) {
                return false;
            }
        }
        return true;
    }

    /** Only the boarding position must fit. Entity collision boxes never obstruct a zipline. */
    public static boolean isClear(Level level, LivingEntity rider, Vec3 feet) {
        AABB bounds = riderBounds(rider, feet).deflate(CONTACT_GAP);
        return withinLoadedWorld(level, bounds) && level.noBlockCollision(rider, bounds);
    }

    /** Sweep the whole rider along the cable, stopping at first contact without sliding off it. */
    public static double moveProgress(Level level, LivingEntity rider, ZiplinePath path, double from, double to) {
        AABB bounds = riderBounds(rider, path.riderPosition(from));
        Vec3 movement = path.riderPosition(to).subtract(path.riderPosition(from));
        AABB swept = bounds.expandTowards(movement);
        if (!withinLoadedWorld(level, swept)) {
            // Wait rather than loading chunks or stepping outside the playable world.
            return from;
        }
        double fraction = 1.0;
        for (VoxelShape shape : level.getBlockCollisions(rider, swept)) {
            for (AABB obstacle : shape.toAabbs()) {
                fraction = Math.min(fraction, collisionFraction(bounds, movement, obstacle));
            }
        }
        if (fraction < 1.0 && movement.lengthSqr() > 0.0) {
            fraction = Math.max(0.0, fraction - CONTACT_GAP / movement.length());
        }
        return from + (to - from) * fraction;
    }

    /** Continuous swept-AABB test: catches thin shapes and works in both directions on slopes. */
    static double collisionFraction(AABB bounds, Vec3 movement, AABB obstacle) {
        double entry = Double.NEGATIVE_INFINITY;
        double exit = Double.POSITIVE_INFINITY;
        for (Direction.Axis axis : Direction.Axis.values()) {
            double delta = movement.get(axis);
            double min = bounds.min(axis);
            double max = bounds.max(axis);
            double obstacleMin = obstacle.min(axis);
            double obstacleMax = obstacle.max(axis);
            if (Math.abs(delta) < 1.0E-12) {
                // Touching a parallel surface (e.g. the floor) doesn't block forward movement.
                if (max <= obstacleMin || min >= obstacleMax) {
                    return 1.0;
                }
            } else {
                double first = (obstacleMin - max) / delta;
                double last = (obstacleMax - min) / delta;
                entry = Math.max(entry, Math.min(first, last));
                exit = Math.min(exit, Math.max(first, last));
            }
        }
        if (entry >= exit || exit <= 0.0 || entry >= 1.0) {
            return 1.0;
        }
        return Math.max(0.0, entry);
    }
}
