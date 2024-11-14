package com.lovetropics.extras.rejoiner;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;

import java.util.function.IntFunction;

public enum AutoRejoinIntent {
	DISABLE(0),
	ENABLE(1),
	RESTART_SERVER(2),
	RESTART_SERVER_AND_CLIENT(3),
	SHUT_DOWN_PERMANENT(4),
	;

	public static final IntFunction<AutoRejoinIntent> BY_ID = ByIdMap.continuous(i -> i.id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
	public static final StreamCodec<ByteBuf, AutoRejoinIntent> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, i -> i.id);

	private final int id;

	AutoRejoinIntent(int id) {
		this.id = id;
	}
}
