package com.lovetropics.extras.network.message;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.client.map.ClientMapManager;
import com.lovetropics.extras.data.poi.MapConfig;
import com.lovetropics.extras.data.poi.PoiConfig;
import com.lovetropics.extras.registry.ExtraRegistries;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundUpdatePoiPacket(
		int networkId,
		ResourceKey<PoiConfig> id,
		Holder<MapConfig> map,
		Component description,
		PoiConfig.Icon icon,
		int markerX,
		int markerY
) implements CustomPacketPayload {
	public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundUpdatePoiPacket> STREAM_CODEC = NeoForgeStreamCodecs.composite(
			ByteBufCodecs.VAR_INT, ClientboundUpdatePoiPacket::networkId,
			ResourceKey.streamCodec(ExtraRegistries.POI), ClientboundUpdatePoiPacket::id,
			MapConfig.STREAM_CODEC, ClientboundUpdatePoiPacket::map,
			ComponentSerialization.STREAM_CODEC, ClientboundUpdatePoiPacket::description,
			PoiConfig.Icon.STREAM_CODEC, ClientboundUpdatePoiPacket::icon,
			ByteBufCodecs.VAR_INT, ClientboundUpdatePoiPacket::markerX,
			ByteBufCodecs.VAR_INT, ClientboundUpdatePoiPacket::markerY,
			ClientboundUpdatePoiPacket::new
	);

	public static final Type<ClientboundUpdatePoiPacket> TYPE = new Type<>(LTExtras.location("update_poi"));

	public static void handle(ClientboundUpdatePoiPacket packet, IPayloadContext context) {
		ClientMapManager.updatePoi(packet.networkId(), packet.id(), packet.map(), packet.description(), packet.icon(), packet.markerX(), packet.markerY());
	}

	@Override
	public Type<ClientboundUpdatePoiPacket> type() {
		return TYPE;
	}
}
