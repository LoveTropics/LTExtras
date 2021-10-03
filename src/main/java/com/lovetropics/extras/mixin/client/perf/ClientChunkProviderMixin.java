package com.lovetropics.extras.mixin.client.perf;

import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.concurrent.atomic.AtomicReferenceArray;

@Mixin(targets = "net/minecraft/client/multiplayer/ClientChunkProvider$ChunkArray")
public class ClientChunkProviderMixin {
    @Shadow @Final private int sideLength;

    @Unique
    private int tableMask;
    @Unique
    private int tableShift;

    @Redirect(method = "<init>(Lnet/minecraft/client/multiplayer/ClientChunkProvider;I)V", at = @At(value = "NEW", target = "Ljava/util/concurrent/atomic/AtomicReferenceArray;<init>(I)V"))
    private AtomicReferenceArray<Chunk> createChunkArray(int length) {
        int tableSize = MathHelper.smallestEncompassingPowerOfTwo(this.sideLength);
        this.tableMask = tableSize - 1;
        this.tableShift = MathHelper.log2(tableSize);
        return new AtomicReferenceArray<>(tableSize * tableSize);
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
