package com.lovetropics.extras.client;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.client.screen.ServerClosedScreen;
import com.lovetropics.extras.rejoiner.AutoRejoinIntent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.DisconnectionDetails;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

import javax.annotation.Nullable;

@EventBusSubscriber(value = Dist.CLIENT, modid = LTExtras.MODID)
public class ClientAutoRejoinHandler {
	private static AutoRejoinIntent intent = AutoRejoinIntent.DISABLE;

	public static void handleIntent(AutoRejoinIntent intent) {
		ClientAutoRejoinHandler.intent = intent;
	}

	@SubscribeEvent
	public static void onDisconnect(ClientPlayerNetworkEvent.LoggingOut event) {
		intent = AutoRejoinIntent.DISABLE;
	}

	@Nullable
	public static Screen createConnectionClosedScreen(DisconnectionDetails details, ServerData server) {
		ServerClosedScreen.Type type = switch (ClientAutoRejoinHandler.intent) {
			case DISABLE -> null;
			case ENABLE -> ServerClosedScreen.Type.UNEXPECTED;
			case RESTART_SERVER -> ServerClosedScreen.Type.SERVER_RESTART;
			case RESTART_SERVER_AND_CLIENT -> ServerClosedScreen.Type.CLIENT_RESTART;
			case SHUT_DOWN_PERMANENT -> ServerClosedScreen.Type.PERMANENT;
		};
		if (type != null) {
			return new ServerClosedScreen(type, details, server);
		}
		return null;
	}
}
