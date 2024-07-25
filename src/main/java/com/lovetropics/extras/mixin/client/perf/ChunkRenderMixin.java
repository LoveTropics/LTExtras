package com.lovetropics.extras.mixin.client.perf;

import com.lovetropics.extras.perf.ChunkRendererExt;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SectionRenderDispatcher.RenderSection.class)
public abstract class ChunkRenderMixin implements ChunkRendererExt {
	@Shadow @Final private BlockPos.MutableBlockPos[] relativeOrigins;

	@Shadow
	protected abstract double getDistToPlayerSqr();

	@Shadow
	protected abstract boolean doesChunkExistAt(BlockPos blockPosIn);

	@Unique
	private boolean neighborChunksLoaded;

	/**
	 * @reason avoid checking neighbor chunk loaded state until a neighbor is unloaded again
	 * @author Gegy
	 */
	@Overwrite
	public boolean hasAllNeighbors() {
		if (neighborChunksLoaded) {
			return true;
		}

		if (getDistToPlayerSqr() > 24.0 * 24.0) {
			neighborChunksLoaded = doesChunkExistAt(relativeOrigins[Direction.WEST.ordinal()])
					&& doesChunkExistAt(relativeOrigins[Direction.NORTH.ordinal()])
					&& doesChunkExistAt(relativeOrigins[Direction.EAST.ordinal()])
					&& doesChunkExistAt(relativeOrigins[Direction.SOUTH.ordinal()]);
			return neighborChunksLoaded;
		} else {
			return true;
		}
	}

	@Inject(method = "setOrigin", at = @At("HEAD"))
	private void setOrigin(int x, int y, int z, CallbackInfo ci) {
		neighborChunksLoaded = false;
	}

	@Override
	public void extras$markNeighborChunksUnloaded() {
		neighborChunksLoaded = false;
	}
}
