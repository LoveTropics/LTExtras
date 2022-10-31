package com.lovetropics.extras.perf;

import it.unimi.dsi.fastutil.HashCommon;
import net.minecraft.util.Mth;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.ChunkAccess;

import javax.annotation.Nullable;
import java.util.Arrays;

public final class LossyChunkCache {
	private static final int COORD_BITS = 30;
	private static final int COORD_MASK = (1 << COORD_BITS) - 1;
	private static final int STATUS_BITS = 4;
	private static final int STEP_MASK = (1 << STATUS_BITS) - 1;

	private final int mask;

	private final long[] keys;
	private final ChunkAccess[] values;

	public LossyChunkCache(int capacity) {
		capacity = Mth.smallestEncompassingPowerOfTwo(capacity);
		this.mask = capacity - 1;

		this.keys = new long[capacity];
		this.values = new ChunkAccess[capacity];
	}

	public void clear() {
		Arrays.fill(this.keys, Long.MIN_VALUE);
		Arrays.fill(this.values, null);
	}

	public void put(int x, int z, ChunkStatus status, ChunkAccess chunk) {
		if (chunk == null) {
			return;
		}

		long key = key(x, z, status);
		int index = this.index(key);

		this.keys[index] = key;
		this.values[index] = chunk;
	}

	@Nullable
	public ChunkAccess get(int x, int z, ChunkStatus step) {
		long key = key(x, z, step);
		int index = this.index(key);

		if (this.keys[index] == key) {
			return this.values[index];
		}

		return null;
	}

	private static long key(int x, int z, ChunkStatus step) {
		return (long) (x & COORD_MASK) << 34
				| (long) (z & COORD_MASK) << 4
				| (step.getIndex() & STEP_MASK);
	}

	private int index(long key) {
		return (int) HashCommon.mix(key) & this.mask;
	}
}
