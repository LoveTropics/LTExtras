package com.lovetropics.extras.client.entity.state;

import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

public class BlockFormedEntityRenderState extends EntityRenderState {
    public Map<BlockPos, BlockModelRenderState> statesToRender = new HashMap<>();
}
