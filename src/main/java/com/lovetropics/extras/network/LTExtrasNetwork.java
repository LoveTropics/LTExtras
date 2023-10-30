package com.lovetropics.extras.network;

import com.lovetropics.extras.LTExtras;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class LTExtrasNetwork {

	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(LTExtras.MODID, "main"),
			LTExtras::getCompatVersion,
			LTExtras::isCompatibleVersion,
			LTExtras::isCompatibleVersion
	);

	public static void register() {
		CHANNEL.messageBuilder(CollectiblesListPacket.class, 0, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(CollectiblesListPacket::write)
				.decoder(CollectiblesListPacket::new)
				.consumerMainThread(CollectiblesListPacket::handle)
				.add();

		CHANNEL.messageBuilder(PickCollectibleItemPacket.class, 1, NetworkDirection.PLAY_TO_SERVER)
				.encoder(PickCollectibleItemPacket::write)
				.decoder(PickCollectibleItemPacket::new)
				.consumerMainThread(PickCollectibleItemPacket::handle)
				.add();

		CHANNEL.messageBuilder(ReturnCollectibleItemPacket.class, 2, NetworkDirection.PLAY_TO_SERVER)
				.encoder(ReturnCollectibleItemPacket::write)
				.decoder(ReturnCollectibleItemPacket::new)
				.consumerMainThread(ReturnCollectibleItemPacket::handle)
				.add();

		CHANNEL.messageBuilder(SetTimeZonePacket.class, 3, NetworkDirection.PLAY_TO_SERVER)
				.encoder(SetTimeZonePacket::write)
				.decoder(SetTimeZonePacket::read)
				.consumerMainThread(SetTimeZonePacket::handle)
				.add();

		CHANNEL.messageBuilder(SetHologramTextPacket.class, 4, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(SetHologramTextPacket::write)
				.decoder(SetHologramTextPacket::new)
				.consumerMainThread(SetHologramTextPacket::handle)
				.add();

		CHANNEL.messageBuilder(SetSkyColorPacket.class, 5, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(SetSkyColorPacket::write)
				.decoder(SetSkyColorPacket::new)
				.consumerMainThread(SetSkyColorPacket::handle)
				.add();

		CHANNEL.messageBuilder(WorldParticleEffectsPacket.class, 6, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(WorldParticleEffectsPacket::write)
				.decoder(WorldParticleEffectsPacket::new)
				.consumerMainThread(WorldParticleEffectsPacket::handle)
				.add();

		CHANNEL.messageBuilder(ClientboundPoiPacket.class, 7, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(ClientboundPoiPacket::write)
				.decoder(ClientboundPoiPacket::new)
				.consumerMainThread(ClientboundPoiPacket::handle)
				.add();

		CHANNEL.messageBuilder(OpenCollectibleBasketPacket.class, 8, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(OpenCollectibleBasketPacket::write)
				.decoder(OpenCollectibleBasketPacket::new)
				.consumerMainThread(OpenCollectibleBasketPacket::handle)
				.add();
	}
}

