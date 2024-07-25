package com.lovetropics.extras.mixin.client.perf;

import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.util.Mth;
import net.minecraft.world.level.chunk.LevelChunk;
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

@Mixin(targets = "net/minecraft/client/multiplayer/ClientChunkCache$Storage")
public class ClientChunkCacheStorageMixin {
	@Shadow @Final @Mutable private AtomicReferenceArray<LevelChunk> chunks;
	@Shadow @Final private int viewRange;

	@Unique
	private int tableMask;
	@Unique
	private int tableShift;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void init(ClientChunkCache chunkProvider, int viewDistance, CallbackInfo ci) {
		int tableSize = Mth.smallestEncompassingPowerOfTwo(viewRange);
		tableMask = tableSize - 1;
		tableShift = Mth.log2(tableSize);
		chunks = new AtomicReferenceArray<>(tableSize * tableSize);
	}

	/**
	 * @reason replace chunk array with power-of-two-sized table for fast indexing
	 * @author Gegy
	 */
	@Overwrite
	int getIndex(int x, int z) {
		int mask = tableMask;
		int shift = tableShift;
		return (x & mask) << shift | (z & mask);
	}
}
