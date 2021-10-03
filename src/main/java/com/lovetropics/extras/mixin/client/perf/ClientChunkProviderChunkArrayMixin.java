package com.lovetropics.extras.mixin.client.perf;

import net.minecraft.client.multiplayer.ClientChunkProvider;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicReferenceArray;

@Mixin(targets = "net/minecraft/client/multiplayer/ClientChunkProvider$ChunkArray")
public class ClientChunkProviderChunkArrayMixin {
    @Shadow @Final @Mutable private AtomicReferenceArray<Chunk> chunks;
    @Shadow @Final private int sideLength;

    @Unique
    private int tableMask;
    @Unique
    private int tableShift;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(ClientChunkProvider chunkProvider, int viewDistance, CallbackInfo ci) {
        int tableSize = MathHelper.smallestEncompassingPowerOfTwo(this.sideLength);
        this.tableMask = tableSize - 1;
        this.tableShift = MathHelper.log2(tableSize);
        this.chunks = new AtomicReferenceArray<>(tableSize * tableSize);
    }

    /**
     * @reason replace chunk array with power-of-two-sized table for fast indexing
     * @author Gegy
     */
    @Overwrite
    private int getIndex(int x, int z) {
        int mask = this.tableMask;
        int shift = this.tableShift;
        return (x & mask) << shift | (z & mask);
    }
}
