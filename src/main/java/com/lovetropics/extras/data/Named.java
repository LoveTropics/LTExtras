package com.lovetropics.extras.data;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.Registry;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public record Named<T>(ResourceKey<T> key, T value) {
	public static <B extends ByteBuf, T> StreamCodec<B, Named<T>> streamCodec(ResourceKey<Registry<T>> registryKey, StreamCodec<B, T> valueCodec) {
		return StreamCodec.composite(
				ResourceKey.streamCodec(registryKey), Named::key,
				valueCodec, Named::value,
				Named::new
		);
	}

	@Override
	public String toString() {
		return id().toString();
	}

	public ResourceLocation id() {
		return key.location();
	}
}
