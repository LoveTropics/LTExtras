package com.lovetropics.extras.mixin.client.perf;

import com.lovetropics.extras.perf.ChunkRendererExt;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public class ClientWorldMixin {
	@Unique
	private static final Direction[] ltextras$HORIZONTALS = new Direction[] { Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST };

	@Shadow @Final private LevelRenderer levelRenderer;

	@Inject(method = "unload", at = @At("HEAD"))
	private void onChunkUnloaded(LevelChunk chunk, CallbackInfo ci) {
		ChunkPos chunkPos = chunk.getPos();
		BlockPos pos = new BlockPos(chunkPos.getMinBlockX(), 0, chunkPos.getMinBlockZ());

		WorldRendererAccess worldRenderer = (WorldRendererAccess) levelRenderer;
		ViewFrustumAccess frustum = (ViewFrustumAccess) worldRenderer.getViewFrustum();

		SectionRenderDispatcher.RenderSection renderChunk = frustum.ltextras$getRenderChunk(pos);
		if (renderChunk != null) {
			for (Direction horizontal : ltextras$HORIZONTALS) {
				BlockPos neighborPos = renderChunk.getRelativeOrigin(horizontal);
				SectionRenderDispatcher.RenderSection neighborChunk = frustum.ltextras$getRenderChunk(neighborPos);
				if (neighborChunk != null) {
					((ChunkRendererExt) neighborChunk).extras$markNeighborChunksUnloaded();
				}
			}
		}
	}
}
