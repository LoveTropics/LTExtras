package com.lovetropics.extras.mixin.client.perf;

import net.minecraft.client.renderer.ViewArea;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ViewArea.class)
public interface ViewFrustumAccess {
	@Invoker("getRenderChunkAt")
	ChunkRenderDispatcher.RenderChunk ltextras$getRenderChunk(BlockPos pos);
}
