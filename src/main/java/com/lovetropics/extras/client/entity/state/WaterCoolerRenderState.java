package com.lovetropics.extras.client.entity.state;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.AnimationState;

/**
 * LTExtras
 * FIRE EMOJI, FIRE EMOJI, FIRE EMOJI
 */
public class WaterCoolerRenderState extends EntityRenderState {
    public final AnimationState shake1AnimationState = new AnimationState();
    public final AnimationState shakeDispenseAnimationState = new AnimationState();
    public float bodyRot;
    public float yRot;
    public float xRot;
    public int shakeTime = 0;

    public WaterCoolerRenderState() {
        this.bodyRot = 0.0f;
        this.boundingBoxWidth = 1.0f;
        this.boundingBoxHeight = 1.75f;
    }
}
