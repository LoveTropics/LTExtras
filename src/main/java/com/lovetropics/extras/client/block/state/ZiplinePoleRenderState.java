package com.lovetropics.extras.client.block.state;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;

import java.util.ArrayList;
import java.util.List;

public class ZiplinePoleRenderState extends BlockEntityRenderState {
    public final List<Rope> ropes = new ArrayList<>();

    /**
     * @param points xyz of each point along the rope, relative to the pole
     * @param light  packed light at each point
     */
    public record Rope(float[] points, int[] light) {
    }
}
