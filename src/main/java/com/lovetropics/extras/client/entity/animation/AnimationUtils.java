package com.lovetropics.extras.client.entity.animation;

import net.minecraft.util.Mth;

public class AnimationUtils {
    public static float squareSin(float theta, float squareness) {
        float modifiedSin = squareness * Mth.sin(theta);
        return modifiedSin / Mth.length(Mth.cos(theta), modifiedSin);
    }
}
