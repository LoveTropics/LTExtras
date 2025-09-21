package com.lovetropics.extras.block;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

// Extremely simplistic, we could do a lot better
public class TrajectorySolver {
    public static final TrajectorySolver STANDARD = new TrajectorySolver(0.08, 0.91, 0.98);

    private static final double SOLVER_STEP = 0.01;
    private static final double GROUND_FRICTION = 0.6;

    private final double gravity;
    private final double frictionX;
    private final double frictionY;

    public TrajectorySolver(double gravity, double frictionX, double frictionY) {
        this.gravity = gravity;
        this.frictionX = frictionX;
        this.frictionY = frictionY;
    }

    public double solveVelocity(double initialHeight, double theta, double distance, boolean startOnGround, double maxVelocity) {
        double cos = Math.cos(theta);
        double sin = Math.sin(theta);
        return Mth.binarySearch(0, Mth.ceil(maxVelocity / SOLVER_STEP), index -> {
            double velocity = index * SOLVER_STEP;
            double simulatedDistance = simulateDistance(initialHeight, cos * velocity, sin * velocity, startOnGround);
            return distance <= simulatedDistance;
        }) * SOLVER_STEP;
    }

    public Vec3 solveVelocity(Vec3 origin, Vec3 target, double theta, boolean startOnGround, double maxVelocity) {
        double deltaX = target.x - origin.x;
        double deltaZ = target.z - origin.z;
        double distance = Mth.length(deltaX, deltaZ);

        double initialY = origin.y - target.y;
        double magnitude = solveVelocity(initialY, theta, distance, startOnGround, maxVelocity);

        double cos = Math.cos(theta);
        double sin = Math.sin(theta);
        return new Vec3(
                magnitude * deltaX / distance * cos,
                magnitude * sin,
                magnitude * deltaZ / distance * cos
        );
    }

    private double simulateDistance(double initialY, double velocityX, double velocityY, boolean onGroundInFirstTick) {
        double x = 0.0;
        double y = initialY;
        int tick = 0;
        do {
            x += velocityX;
            y += velocityY;
            double stepFrictionX = tick == 0 && onGroundInFirstTick ? GROUND_FRICTION * frictionX : frictionX;
            velocityX = velocityX * stepFrictionX;
            velocityY = (velocityY - gravity) * frictionY;
            tick++;
        } while (y > 0.0 || velocityY > 0.0);
        return x;
    }
}
