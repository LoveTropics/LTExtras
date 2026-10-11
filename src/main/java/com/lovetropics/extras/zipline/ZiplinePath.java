package com.lovetropics.extras.zipline;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.LinkedHashSet;
import java.util.OptionalDouble;
import java.util.function.DoublePredicate;

/** Shared by movement, picking and the taut vanilla leash renderer. */
public record ZiplinePath(Vec3 start, Vec3 end) {
    public static final double MAX_LENGTH = 64.0;
    public static final double PICK_RADIUS = 0.2;
    public static final double RIDER_DROP = 1.8;
    public static final double END_CLEARANCE = 1.25;
    public static final double BOARDING_CLEARANCE = 0.5;
    public static final double BOARDING_SEARCH_STEP = 0.5;
    public static final double BOARDING_SEARCH_DISTANCE = 2.0;

    public double length() {
        return start.distanceTo(end);
    }

    /** A steady bonus based on the full endpoint drop, never compounded per tick or per block. */
    public double speedMultiplier(boolean towardEnd) {
        double drop = towardEnd ? start.y - end.y : end.y - start.y;
        return 1.0 + Math.min(1.0, Math.max(0.0, drop) * 0.03);
    }

    public boolean isValid() {
        Vec3 delta = end.subtract(start);
        // A horizontal span also avoids the vanilla leash renderer's vertical-line singularity.
        return delta.horizontalDistanceSqr() >= 9.0 && length() <= MAX_LENGTH;
    }

    public double nearestProgress(Vec3 point) {
        Vec3 delta = end.subtract(start);
        double squaredLength = delta.lengthSqr();
        return squaredLength < 1.0E-8 ? 0.0 : Mth.clamp(point.subtract(start).dot(delta) / squaredLength, 0.0, 1.0);
    }

    public Vec3 point(double progress) {
        return start.lerp(end, progress);
    }

    public Vec3 riderPosition(double progress) {
        return point(progress).add(0.0, -RIDER_DROP, 0.0);
    }

    public double boardingProgress(Vec3 clickedPoint) {
        return boardingProgress(clickedPoint, 0.6);
    }

    public double boardingProgress(Vec3 clickedPoint, double riderWidth) {
        Vec3 delta = end.subtract(start);
        // The pole is half a block wide. Account for the rider too, including diagonal/sloped lines.
        double horizontalSpan = Math.max(Math.abs(delta.x), Math.abs(delta.z));
        double poleClearance = (0.25 + riderWidth / 2.0 + 1.0E-5) / horizontalSpan;
        double margin = Math.min(0.5, Math.max(BOARDING_CLEARANCE / length(), poleClearance));
        return Mth.clamp(nearestProgress(clickedPoint), margin, 1.0 - margin);
    }

    public double destinationProgress(double boardingProgress) {
        return destinationProgress(boardingProgress, boardingProgress <= 0.5);
    }

    /** Follow the view within 45 degrees of either cable direction; otherwise use the farther end. */
    public double destinationProgress(double boardingProgress, Vec3 lookDirection) {
        return destinationProgress(boardingProgress, travelsTowardEnd(boardingProgress, lookDirection));
    }

    public boolean travelsTowardEnd(double boardingProgress, Vec3 lookDirection) {
        double cableLength = length();
        if (cableLength > 5.0 && Math.min(boardingProgress, 1.0 - boardingProgress) * cableLength < 3.0) {
            // On 5-6 block cables these zones overlap; use the nearer endpoint (start wins ties).
            return boardingProgress <= 0.5;
        }
        double alignment = lookDirection.normalize().dot(end.subtract(start).normalize());
        return Math.abs(alignment) >= Math.sqrt(0.5) ? alignment > 0.0 : boardingProgress <= 0.5;
    }

    public double destinationProgress(double boardingProgress, boolean towardEnd) {
        double margin = END_CLEARANCE / length();
        // A click inside the arrival clearance must not send the rider opposite their view.
        return towardEnd ? Math.max(boardingProgress, 1.0 - margin) : Math.min(boardingProgress, margin);
    }

    /** Search from the click, exhausting the preferred side before considering the opposite side. */
    public OptionalDouble findBoardingProgress(Vec3 clickedPoint, double riderWidth, boolean towardEnd, DoublePredicate isClear) {
        double clicked = nearestProgress(clickedPoint);
        var candidates = new LinkedHashSet<Double>();
        candidates.add(boardingProgress(point(clicked), riderWidth));
        double direction = towardEnd ? 1.0 : -1.0;
        int steps = (int) Math.round(BOARDING_SEARCH_DISTANCE / BOARDING_SEARCH_STEP);
        for (double sign : new double[]{direction, -direction}) {
            for (int step = 1; step <= steps; step++) {
                double candidate = clicked + sign * step * BOARDING_SEARCH_STEP / length();
                if (candidate >= 0.0 && candidate <= 1.0) {
                    candidates.add(boardingProgress(point(candidate), riderWidth));
                }
            }
        }
        for (double candidate : candidates) {
            // Clearance adjustment must not move the search beyond two blocks of the actual click.
            if (Math.abs(candidate - clicked) * length() <= BOARDING_SEARCH_DISTANCE + 1.0E-7 && isClear.test(candidate)) {
                return OptionalDouble.of(candidate);
            }
        }
        return OptionalDouble.empty();
    }

    /** Closest points on a finite eye ray and the finite cable. No oversized entity hitbox. */
    public RayHit intersect(Vec3 eye, Vec3 rayEnd) {
        Vec3 ray = rayEnd.subtract(eye);
        Vec3 cable = end.subtract(start);
        Vec3 offset = eye.subtract(start);
        double a = ray.lengthSqr();
        double b = ray.dot(cable);
        double c = cable.lengthSqr();
        double d = ray.dot(offset);
        double e = cable.dot(offset);
        if (a < 1.0E-8 || c < 1.0E-8) {
            return new RayHit(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 0.0);
        }
        double denominator = a * c - b * b;
        double rayProgress = denominator > 1.0E-8 ? Mth.clamp((b * e - c * d) / denominator, 0.0, 1.0) : 0.0;
        double cableProgress = (b * rayProgress + e) / c;
        if (cableProgress < 0.0) {
            cableProgress = 0.0;
            rayProgress = Mth.clamp(-d / a, 0.0, 1.0);
        } else if (cableProgress > 1.0) {
            cableProgress = 1.0;
            rayProgress = Mth.clamp((b - d) / a, 0.0, 1.0);
        }
        return new RayHit(rayProgress * Math.sqrt(a), eye.add(ray.scale(rayProgress)).distanceTo(point(cableProgress)), cableProgress);
    }

    public record RayHit(double distanceAlongRay, double distanceToCable, double cableProgress) {
        public boolean hits() {
            return distanceToCable <= PICK_RADIUS;
        }
    }
}
