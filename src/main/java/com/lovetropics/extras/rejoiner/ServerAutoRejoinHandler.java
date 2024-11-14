package com.lovetropics.extras.rejoiner;

import com.lovetropics.extras.ExtrasConfig;
import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.network.message.ClientboundSetAutoRejoinIntent;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import static net.minecraft.commands.Commands.literal;

@EventBusSubscriber(modid = LTExtras.MODID)
public class ServerAutoRejoinHandler {
	@SubscribeEvent
	public static void onRegisterCommands(RegisterCommandsEvent event) {
		event.getDispatcher().register(literal("stop")
				.then(literal("restartClient").executes(context -> stopWithIntent(context, AutoRejoinIntent.RESTART_SERVER_AND_CLIENT)))
				.then(literal("permanently").executes(context -> stopWithIntent(context, AutoRejoinIntent.SHUT_DOWN_PERMANENT)))
		);
	}

	private static int stopWithIntent(CommandContext<CommandSourceStack> context, AutoRejoinIntent restartServerAndClient) {
		MinecraftServer server = context.getSource().getServer();
		broadcastRejoinIntent(server, restartServerAndClient);
		server.halt(false);
		return 1;
	}

	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		if (event.getEntity() instanceof ServerPlayer player) {
			AutoRejoinIntent intent = ExtrasConfig.CLIENT_AUTO_REJOIN.get() ? AutoRejoinIntent.ENABLE : AutoRejoinIntent.DISABLE;
			player.connection.send(new ClientboundSetAutoRejoinIntent(intent));
		}
	}

	public static void broadcastRejoinIntent(MinecraftServer server, AutoRejoinIntent intent) {
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			player.connection.send(new ClientboundSetAutoRejoinIntent(intent));
		}
	}
}
