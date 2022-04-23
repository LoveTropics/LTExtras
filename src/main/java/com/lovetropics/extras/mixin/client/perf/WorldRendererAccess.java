package com.lovetropics.extras.mixin.client.perf;

import net.minecraft.client.renderer.ViewArea;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LevelRenderer.class)
public interface WorldRendererAccess {
    @Accessor("viewArea")
    ViewArea getViewFrustum();
}
