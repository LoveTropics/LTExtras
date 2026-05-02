package com.lovetropics.extras.client.entity.state;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.network.chat.Component;

import java.util.Optional;

public class SpinningSignRenderState extends EntityRenderState {
    public float yRot;
    public float scale;
    public Optional<Component> text = Optional.empty();
    public ItemStackRenderState itemStack =  new ItemStackRenderState();
}
