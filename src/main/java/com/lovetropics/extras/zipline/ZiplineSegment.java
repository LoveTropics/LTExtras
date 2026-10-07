package com.lovetropics.extras.zipline;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/**
 * The rope between two connected poles, oriented from {@link #from()} to {@link #to()}, even though it functionally doesn't really have an orientation.
 * The rope is modelled as a parabola which droops below the straight line between both anchors depending on its slack.
 */
public final class ZiplineSegment {
    public static final int MAX_SPAN = 128;
    public static final int MAX_SLACK = 10;
    public static final int DEFAULT_SLACK = 2;
    public static final double ANCHOR_HEIGHT = 2.0;
    private static final double SAG_PER_SLACK = 0.02;
    private static final double EPSILON = 1.0e-7;

    private final BlockPos from;
    private final BlockPos to;
    private final int slack;
    private final Vec3 start;
    private final Vec3 end;
    private final double sag;

    public ZiplineSegment(BlockPos from, BlockPos to, int slack) {
        this.from = from;
        this.to = to;
        this.slack = slack;
        start = anchor(from);
        end = anchor(to);
        sag = start.distanceTo(end) * slack * SAG_PER_SLACK;
    }

    public static Vec3 anchor(BlockPos pole) {
        return new Vec3(pole.getX() + 0.5, pole.getY() + ANCHOR_HEIGHT, pole.getZ() + 0.5);
    }

    public BlockPos from() {
        return from;
    }

    public BlockPos to() {
        return to;
    }

    public int slack() {
        return slack;
    }

    public ZiplineSegment reversed() {
        return new ZiplineSegment(to, from, slack);
    }

    public Vec3 point(double t) {
        return new Vec3(
                Mth.lerp(t, start.x, end.x),
                Mth.lerp(t, start.y, end.y) - 4.0 * sag * t * (1.0 - t),
                Mth.lerp(t, start.z, end.z)
        );
    }

    public Vec3 derivative(double t) {
        return new Vec3(end.x - start.x, end.y - start.y - 4.0 * sag * (1.0 - 2.0 * t), end.z - start.z);
    }

    public Vec3 tangent(double t) {
        return derivative(t).normalize();
    }

    public int sampleCount() {
        return Mth.clamp(Mth.ceil(start.distanceTo(end)), 4, 128);
    }

    public AABB bounds() {
        return new AABB(start, end).expandTowards(0.0, -sag, 0.0);
    }

    public @Nullable Hit raycast(Vec3 rayStart, Vec3 rayEnd, double radius) {
        int samples = sampleCount();
        double rayLength = rayStart.distanceTo(rayEnd);
        Hit closest = null;
        Vec3 previous = start;
        for (int i = 1; i <= samples; i++) {
            Vec3 current = point((double) i / samples);
            Closest result = closestBetween(rayStart, rayEnd, previous, current);
            if (result.distanceSq() <= radius * radius) {
                double distance = result.s() * rayLength;
                if (closest == null || distance < closest.distance()) {
                    closest = new Hit(this, (i - 1 + result.u()) / samples, distance);
                }
            }
            previous = current;
        }
        return closest;
    }

    public double closestT(Vec3 target) {
        int samples = sampleCount();
        double closestT = 0.0;
        double closestDistanceSq = Double.MAX_VALUE;
        Vec3 previous = start;
        for (int i = 1; i <= samples; i++) {
            Vec3 current = point((double) i / samples);
            Closest result = closestBetween(target, target, previous, current);
            if (result.distanceSq() < closestDistanceSq) {
                closestDistanceSq = result.distanceSq();
                closestT = (i - 1 + result.u()) / samples;
            }
            previous = current;
        }
        return closestT;
    }

    public double distanceToSqr(Vec3 target) {
        return target.distanceToSqr(point(closestT(target)));
    }

    // Closest points between the line segments p1-q1 and p2-q2, as parameters s and u along each segment
    private static Closest closestBetween(Vec3 p1, Vec3 q1, Vec3 p2, Vec3 q2) {
        Vec3 d1 = q1.subtract(p1);
        Vec3 d2 = q2.subtract(p2);
        Vec3 r = p1.subtract(p2);
        double a = d1.lengthSqr();
        double e = d2.lengthSqr();
        double f = d2.dot(r);
        double s;
        double u;
        if (a <= EPSILON && e <= EPSILON) {
            s = 0.0;
            u = 0.0;
        } else if (a <= EPSILON) {
            s = 0.0;
            u = Mth.clamp(f / e, 0.0, 1.0);
        } else {
            double c = d1.dot(r);
            if (e <= EPSILON) {
                u = 0.0;
                s = Mth.clamp(-c / a, 0.0, 1.0);
            } else {
                double b = d1.dot(d2);
                double denominator = a * e - b * b;
                s = denominator > EPSILON ? Mth.clamp((b * f - c * e) / denominator, 0.0, 1.0) : 0.0;
                u = (b * s + f) / e;
                if (u < 0.0) {
                    u = 0.0;
                    s = Mth.clamp(-c / a, 0.0, 1.0);
                } else if (u > 1.0) {
                    u = 1.0;
                    s = Mth.clamp((b - c) / a, 0.0, 1.0);
                }
            }
        }
        Vec3 closest1 = p1.add(d1.scale(s));
        Vec3 closest2 = p2.add(d2.scale(u));
        return new Closest(s, u, closest1.distanceToSqr(closest2));
    }

    private record Closest(double s, double u, double distanceSq) {
    }

    /**
     * @param t        position along the segment
     * @param distance distance along the ray to the hit
     */
    public record Hit(ZiplineSegment segment, double t, double distance) {
    }
}
