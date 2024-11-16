package com.lovetropics.extras.data;

import net.minecraft.resources.ResourceLocation;

public record Named<T>(ResourceLocation id, T value) {
	@Override
	public String toString() {
		return id.toString();
	}
}
