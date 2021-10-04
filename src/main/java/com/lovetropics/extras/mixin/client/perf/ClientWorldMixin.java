package com.lovetropics.extras.mixin.client.perf;

import com.lovetropics.extras.perf.ChunkRendererExt;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {
    private static final Direction[] HORIZONTALS = new Direction[] { Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST };

    @Shadow @Final private WorldRenderer worldRenderer;

    @Inject(method = "onChunkUnloaded", at = @At("HEAD"))
    private void onChunkUnloaded(Chunk chunk, CallbackInfo ci) {
        ChunkPos chunkPos = chunk.getPos();
        BlockPos pos = new BlockPos(chunkPos.getXStart(), 0, chunkPos.getZStart());

        WorldRendererAccess worldRenderer = (WorldRendererAccess) this.worldRenderer;
        ViewFrustumAccess frustum = (ViewFrustumAccess) worldRenderer.getViewFrustum();

        ChunkRenderDispatcher.ChunkRender renderChunk = frustum.ltextras$getRenderChunk(pos);
        if (renderChunk != null) {
            for (Direction horizontal : HORIZONTALS) {
                BlockPos neighborPos = renderChunk.getBlockPosOffset16(horizontal);
                ChunkRenderDispatcher.ChunkRender neighborChunk = frustum.ltextras$getRenderChunk(neighborPos);
                if (neighborChunk != null) {
                    ((ChunkRendererExt) neighborChunk).extras$markNeighborChunksUnloaded();
                }
            }
        }
    }
}
