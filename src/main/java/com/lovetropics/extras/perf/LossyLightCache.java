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
            this.mask = capacity - 1;

            this.keys = new long[capacity];
            this.values = new int[capacity];
        }

        public void clear() {
            Arrays.fill(this.keys, Long.MAX_VALUE);
            Arrays.fill(this.values, Integer.MAX_VALUE);
        }

        public void put(long pos, int packedLight) {
            int index = this.index(pos);
            this.keys[index] = pos;
            this.values[index] = packedLight;
        }

        public int get(long pos) {
            int index = this.index(pos);
            if (this.keys[index] == pos) {
                return this.values[index];
            } else {
                return Integer.MAX_VALUE;
            }
        }

        private int index(long key) {
            return (int) HashCommon.mix(key) & this.mask;
        }
    }

    public static final class Brightness {
        private final int mask;

        private final long[] keys;
        private final float[] values;

        public Brightness(int capacity) {
            capacity = Mth.smallestEncompassingPowerOfTwo(capacity);
            this.mask = capacity - 1;

            this.keys = new long[capacity];
            this.values = new float[capacity];
        }

        public void clear() {
            Arrays.fill(this.keys, Long.MAX_VALUE);
            Arrays.fill(this.values, Float.NaN);
        }

        public void put(long pos, float brightness) {
            int index = this.index(pos);
            this.keys[index] = pos;
            this.values[index] = brightness;
        }

        public float get(long pos) {
            int index = this.index(pos);
            if (this.keys[index] == pos) {
                return this.values[index];
            } else {
                return Float.NaN;
            }
        }

        private int index(long key) {
            return (int) HashCommon.mix(key) & this.mask;
        }
    }
}
