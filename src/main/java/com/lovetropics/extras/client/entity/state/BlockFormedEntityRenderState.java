package com.lovetropics.extras.client.entity.state;

import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.core.BlockPos;

import java.util.HashMap;
import java.util.Map;

public class BlockFormedEntityRenderState extends EntityRenderState {
    public Map<BlockPos, BlockModelRenderStateExtended> statesToRender = new HashMap<>();
    public float xRot;
    public float yRot;
    public static class BlockModelRenderStateExtended extends BlockModelRenderState {
        public boolean dontMove = false;
    }
}
