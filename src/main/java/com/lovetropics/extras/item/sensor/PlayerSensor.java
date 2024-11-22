package com.lovetropics.extras.item.sensor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public record PlayerSensor(
		String tag,
		Appearance appearance
) {
	public static final Codec<PlayerSensor> CODEC = RecordCodecBuilder.create(i -> i.group(
			Codec.STRING.fieldOf("tag").forGetter(PlayerSensor::tag),
			Appearance.CODEC.fieldOf("appearance").forGetter(PlayerSensor::appearance)
	).apply(i, PlayerSensor::new));

	public static final StreamCodec<ByteBuf, PlayerSensor> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8, PlayerSensor::tag,
			Appearance.STREAM_CODEC, PlayerSensor::appearance,
			PlayerSensor::new
	);

	public boolean matches(ServerPlayer player) {
		return player.getTags().contains(tag);
	}

	public record Appearance(
			int color,
			Optional<Sprite> faceDecoration
	) {
		public static final Codec<Appearance> CODEC = RecordCodecBuilder.create(i -> i.group(
				Codec.INT.fieldOf("color").forGetter(Appearance::color),
				Sprite.CODEC.optionalFieldOf("face_decoration").forGetter(Appearance::faceDecoration)
		).apply(i, Appearance::new));

		public static final StreamCodec<ByteBuf, Appearance> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.INT, Appearance::color,
				Sprite.STREAM_CODEC.apply(ByteBufCodecs::optional), Appearance::faceDecoration,
				Appearance::new
		);
	}

	public record Sprite(ResourceLocation location, int width, int height) {
		public static final Codec<Sprite> CODEC = RecordCodecBuilder.create(i -> i.group(
				ResourceLocation.CODEC.fieldOf("location").forGetter(Sprite::location),
				Codec.INT.fieldOf("width").forGetter(Sprite::width),
				Codec.INT.fieldOf("height").forGetter(Sprite::height)
		).apply(i, Sprite::new));

		public static final StreamCodec<ByteBuf, Sprite> STREAM_CODEC = StreamCodec.composite(
				ResourceLocation.STREAM_CODEC, Sprite::location,
				ByteBufCodecs.VAR_INT, Sprite::width,
				ByteBufCodecs.VAR_INT, Sprite::height,
				Sprite::new
		);
	}
}
