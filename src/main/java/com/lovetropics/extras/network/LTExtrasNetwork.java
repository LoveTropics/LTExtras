package com.lovetropics.extras.network;

import com.lovetropics.extras.LTExtras;

import net.minecraft.resources.ResourceLocation;
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
	}
}

