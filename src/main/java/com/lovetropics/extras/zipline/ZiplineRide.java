package com.lovetropics.extras.zipline;

import net.minecraft.SharedConstants;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/**
 * An entity sliding along a line of ropes, simulated by whichever side controls the entity's movement.
 */
public final class ZiplineRide {
    private static final double HAND_REACH = 0.2;
    private static final double MAX_DISPLACEMENT = 1.0;
    private static final double GRAVITY = 0.1;
    private static final double DRAG = 0.99;
    private static final double MIN_SPEED = 5.0 / SharedConstants.TICKS_PER_SECOND;
    private static final double MAX_SPEED = 2.0;
    private static final double TURN_ACCELERATION = 0.05;
    private static final double TURN_INPUT_THRESHOLD = -0.5;
    private static final double MAX_STEP = 0.25;

    private ZiplineSegment segment;
    private double t;
    // Blocks per tick along the orientation of the current segment
    private double velocity;
    // Where the rider wants to go along the orientation of the current segment: 1 or -1
    private int direction;
    // Braking against the current velocity before building up speed the other way
    private boolean turning;
    private @Nullable Vec3 lastPosition;

    public ZiplineRide(ZiplineSegment segment, double t, double velocity, int direction) {
        this.segment = segment;
        this.t = Mth.clamp(t, 0.0, 1.0);
        this.velocity = Mth.clamp(velocity, -MAX_SPEED, MAX_SPEED);
        this.direction = direction;
        turning = this.velocity * direction < 0.0;
    }

    public ZiplineSegment segment() {
        return segment;
    }

    public static double hangDistance(Entity entity) {
        return entity.getBbHeight() + HAND_REACH;
    }

    public void positionRider(Entity entity) {
        Vec3 position = segment.point(t).subtract(0.0, hangDistance(entity), 0.0);
        entity.setPos(position);
        entity.setDeltaMovement(motion());
        lastPosition = position;
    }

    /**
     * @return true if something other than the ride moved the rider since it was last positioned
     */
    public boolean isDisplaced(Entity entity) {
        return lastPosition != null && entity.position().distanceToSqr(lastPosition) > MAX_DISPLACEMENT * MAX_DISPLACEMENT;
    }

    public Vec3 motion() {
        return segment.tangent(t).scale(velocity);
    }

    /**
     * @param moveInput horizontal direction that the rider is pressing towards, in world space
     * @return false once the rider has reached the end of the line, or the rope has been removed
     */
    public boolean tick(ZiplineIndex index, Vec3 moveInput) {
        ZiplineSegment current = index.getSegment(segment.from(), segment.to());
        if (current == null) {
            return false;
        }
        segment = current;

        updateDirection(moveInput);

        if (turning) {
            velocity += direction * TURN_ACCELERATION;
            if (velocity * direction >= MIN_SPEED) {
                turning = false;
            }
        } else {
            velocity = (velocity - segment.tangent(t).y * GRAVITY) * DRAG;
            if (velocity * direction < MIN_SPEED) {
                velocity = direction * MIN_SPEED;
            }
        }
        velocity = Mth.clamp(velocity, -MAX_SPEED, MAX_SPEED);

        return advance(index);
    }

    private void updateDirection(Vec3 moveInput) {
        Vec3 tangent = segment.tangent(t);
        double tangentLength = tangent.horizontalDistance();
        double inputLength = moveInput.horizontalDistance();
        if (tangentLength < 1.0e-3 || inputLength < 1.0e-3) {
            return;
        }
        double alignment = (moveInput.x * tangent.x + moveInput.z * tangent.z) * direction / (tangentLength * inputLength);
        if (alignment < TURN_INPUT_THRESHOLD) {
            direction = -direction;
            turning = velocity * direction < MIN_SPEED;
        }
    }

    private boolean advance(ZiplineIndex index) {
        double remaining = Math.abs(velocity);
        int sign = velocity >= 0.0 ? 1 : -1;
        while (remaining > 1.0e-6) {
            double rate = Math.max(segment.derivative(t).length(), 1.0e-3);
            double step = Math.min(remaining, MAX_STEP);
            double nextT = t + sign * step / rate;
            if (nextT >= 0.0 && nextT <= 1.0) {
                t = nextT;
                remaining -= step;
                continue;
            }

            boolean forwards = nextT > 1.0;
            remaining -= Math.abs((forwards ? 1.0 : 0.0) - t) * rate;
            if (forwards) {
                ZiplineSegment next = index.getContinuation(segment.to(), segment.from());
                if (next == null) {
                    t = 1.0;
                    return false;
                }
                segment = next;
                t = 0.0;
            } else {
                ZiplineSegment next = index.getContinuation(segment.from(), segment.to());
                if (next == null) {
                    t = 0.0;
                    return false;
                }
                // Keep travelling towards decreasing t
                segment = next.reversed();
                t = 1.0;
            }
        }
        return true;
    }
}
