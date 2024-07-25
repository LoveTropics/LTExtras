package com.lovetropics.extras.mixin.client.perf;

import net.minecraft.client.renderer.ViewArea;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import javax.annotation.Nullable;

@Mixin(ViewArea.class)
public interface ViewFrustumAccess {
	@Invoker("getRenderSectionAt")
	@Nullable
	SectionRenderDispatcher.RenderSection ltextras$getRenderChunk(BlockPos pos);
}
