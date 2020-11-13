package com.lovetropics.extras.network;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.entity.UpdateDummyTexturesMessage;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class LTExtrasNetwork {

	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(LTExtras.MODID, "main"),
			LTExtras::getCompatVersion,
			LTExtras::isCompatibleVersion,
			LTExtras::isCompatibleVersion
	);

	public static void register() {
		CHANNEL.registerMessage(0, UpdateDummyTexturesMessage.class, UpdateDummyTexturesMessage::toBytes, UpdateDummyTexturesMessage::fromBytes, UpdateDummyTexturesMessage::handle);
	}
}

