package com.lovetropics.extras.item.sensor;

import com.lovetropics.lib.codec.MoreCodecs;
import com.lovetropics.lib.permission.PermissionsApi;
import com.lovetropics.lib.permission.role.Role;
import com.lovetropics.lib.permission.role.RoleReader;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Optional;

public record PlayerSensor(
		List<String> tags,
		List<String> roles,
		Appearance appearance
) {
	public static final Codec<PlayerSensor> CODEC = RecordCodecBuilder.create(i -> i.group(
			MoreCodecs.listOrUnit(Codec.STRING).optionalFieldOf("tag", List.of()).forGetter(PlayerSensor::tags),
			MoreCodecs.listOrUnit(Codec.STRING).optionalFieldOf("roles", List.of()).forGetter(PlayerSensor::roles),
			Appearance.CODEC.fieldOf("appearance").forGetter(PlayerSensor::appearance)
	).apply(i, PlayerSensor::new));

	public static final StreamCodec<ByteBuf, PlayerSensor> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), PlayerSensor::tags,
			ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), PlayerSensor::roles,
			Appearance.STREAM_CODEC, PlayerSensor::appearance,
			PlayerSensor::new
	);

	public boolean matches(ServerPlayer player) {
		for (String tag : tags) {
			if (player.getTags().contains(tag)) {
				return true;
			}
		}
		if (!roles.isEmpty()) {
			RoleReader reader = PermissionsApi.lookup().byPlayer(player);
			for (String roleId : roles) {
				Role role = PermissionsApi.provider().get(roleId);
				if (reader.has(role)) {
					return true;
				}
			}
		}
		return false;
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
