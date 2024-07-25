package com.lovetropics.extras.perf;

import it.unimi.dsi.fastutil.HashCommon;
import net.minecraft.util.Mth;

import java.util.Arrays;

public final class LossyLightCache {
	public static final class Packed {
		private final int mask;

		private final long[] keys;
		private final int[] values;

		public Packed(int capacity) {
			capacity = Mth.smallestEncompassingPowerOfTwo(capacity);
			mask = capacity - 1;

			keys = new long[capacity];
			values = new int[capacity];
		}

		public void clear() {
			Arrays.fill(keys, Long.MAX_VALUE);
			Arrays.fill(values, Integer.MAX_VALUE);
		}

		public void put(long pos, int packedLight) {
			int index = index(pos);
			keys[index] = pos;
			values[index] = packedLight;
		}

		public int get(long pos) {
			int index = index(pos);
			if (keys[index] == pos) {
				return values[index];
			} else {
				return Integer.MAX_VALUE;
			}
		}

		private int index(long key) {
			return (int) HashCommon.mix(key) & mask;
		}
	}

	public static final class Brightness {
		private final int mask;

		private final long[] keys;
		private final float[] values;

		public Brightness(int capacity) {
			capacity = Mth.smallestEncompassingPowerOfTwo(capacity);
			mask = capacity - 1;

			keys = new long[capacity];
			values = new float[capacity];
		}

		public void clear() {
			Arrays.fill(keys, Long.MAX_VALUE);
			Arrays.fill(values, Float.NaN);
		}

		public void put(long pos, float brightness) {
			int index = index(pos);
			keys[index] = pos;
			values[index] = brightness;
		}

		public float get(long pos) {
			int index = index(pos);
			if (keys[index] == pos) {
				return values[index];
			} else {
				return Float.NaN;
			}
		}

		private int index(long key) {
			return (int) HashCommon.mix(key) & mask;
		}
	}
}
