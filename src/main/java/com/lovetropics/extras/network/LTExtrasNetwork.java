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
	}
}

