package com.lovetropics.extras.client.entity.state;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.phys.AABB;

public class ForkliftRenderState extends EntityRenderState {
    public float yRot;
    public double velocity;
    public float forkHeight;
    public AABB pickupAABB;
}
