package com.lovetropics.extras.mixin.client.perf;

import com.lovetropics.extras.perf.ChunkRendererExt;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkRenderDispatcher.ChunkRender.class)
public abstract class ChunkRenderMixin implements ChunkRendererExt {
    @Shadow @Final private BlockPos.Mutable[] mapEnumFacing;

    @Shadow
    protected abstract double getDistanceSq();

    @Shadow
    protected abstract boolean isChunkLoaded(BlockPos blockPosIn);

    @Unique
    private boolean neighborChunksLoaded;

    /**
     * @reason avoid checking neighbor chunk loaded state until a neighbor is unloaded again
     * @author Gegy
     */
    @Overwrite
    public boolean shouldStayLoaded() {
        if (this.neighborChunksLoaded) {
            return true;
        }

        if (this.getDistanceSq() > 24.0 * 24.0) {
            this.neighborChunksLoaded = this.isChunkLoaded(this.mapEnumFacing[Direction.WEST.ordinal()])
                    && this.isChunkLoaded(this.mapEnumFacing[Direction.NORTH.ordinal()])
                    && this.isChunkLoaded(this.mapEnumFacing[Direction.EAST.ordinal()])
                    && this.isChunkLoaded(this.mapEnumFacing[Direction.SOUTH.ordinal()]);
            return this.neighborChunksLoaded;
        } else {
            return true;
        }
    }

    @Inject(method = "setPosition", at = @At("HEAD"))
    private void setPosition(int x, int y, int z, CallbackInfo ci) {
        this.neighborChunksLoaded = false;
    }

    @Override
    public void extras$markNeighborChunksUnloaded() {
        this.neighborChunksLoaded = false;
    }
}
