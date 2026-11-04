package com.lovetropics.extras.client.entity.state;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.core.Direction;

public class SeeSawState extends BlockFormedEntityRenderState {
    public float seeSawAngle = 0f;
    public float prevSeeSawAngle = 0f;
    public Direction direction = Direction.UP;
}
