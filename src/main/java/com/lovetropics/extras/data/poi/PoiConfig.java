package com.lovetropics.extras.data.poi;

import com.lovetropics.lib.codec.MoreCodecs;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import java.util.function.Function;

public record PoiConfig(
		Holder<MapConfig> map,
		Component description,
		Icon icon,
		BlockPos pos,
		Optional<Float> angle
) {
	public static final Codec<PoiConfig> CODEC = RecordCodecBuilder.create(i -> i.group(
			MapConfig.CODEC.fieldOf("map").forGetter(PoiConfig::map),
			ComponentSerialization.CODEC.fieldOf("description").forGetter(PoiConfig::description),
			Icon.CODEC.fieldOf("icon").forGetter(PoiConfig::icon),
			BlockPos.CODEC.fieldOf("pos").forGetter(PoiConfig::pos),
			Codec.FLOAT.optionalFieldOf("angle").forGetter(PoiConfig::angle)
	).apply(i, PoiConfig::new));

	public sealed interface Icon {
		Codec<Icon> CODEC = Codec.either(TextureIcon.CODEC, ItemIcon.CODEC).xmap(
				either -> either.map(Function.identity(), Function.identity()),
				Icon::toEither
		);
		StreamCodec<RegistryFriendlyByteBuf, Icon> STREAM_CODEC = ByteBufCodecs.either(TextureIcon.STREAM_CODEC, ItemIcon.STREAM_CODEC).map(
				either -> either.map(Function.identity(), Function.identity()),
				Icon::toEither
		);

		private static Either<TextureIcon, ItemIcon> toEither(Icon icon) {
			return switch (icon) {
				case TextureIcon texture -> Either.left(texture);
				case ItemIcon item -> Either.right(item);
			};
		}
	}

	public record TextureIcon(ResourceLocation texture) implements Icon {
		public static final Codec<TextureIcon> CODEC = RecordCodecBuilder.create(i -> i.group(
				ResourceLocation.CODEC.fieldOf("texture").forGetter(TextureIcon::texture)
		).apply(i, TextureIcon::new));
		public static final StreamCodec<ByteBuf, TextureIcon> STREAM_CODEC = StreamCodec.composite(
				ResourceLocation.STREAM_CODEC, TextureIcon::texture,
				TextureIcon::new
		);
	}

	public record ItemIcon(ItemStack item) implements Icon {
		public static final Codec<ItemIcon> CODEC = RecordCodecBuilder.create(i -> i.group(
				MoreCodecs.ITEM_STACK.fieldOf("item").forGetter(ItemIcon::item)
		).apply(i, ItemIcon::new));
		public static final StreamCodec<RegistryFriendlyByteBuf, ItemIcon> STREAM_CODEC = StreamCodec.composite(
				ItemStack.STREAM_CODEC, ItemIcon::item,
				ItemIcon::new
		);
	}
}
