package com.lovetropics.extras.mixin.client.perf;

import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.client.renderer.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WorldRenderer.class)
public interface WorldRendererAccess {
    @Accessor("viewArea")
    ViewFrustum getViewFrustum();
}
