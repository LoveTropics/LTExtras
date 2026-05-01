package com.lovetropics.extras.client.block.state;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;

public class DisplayBlockRenderState extends BlockEntityRenderState {
    public ItemStackRenderState itemStack = new ItemStackRenderState();
}
