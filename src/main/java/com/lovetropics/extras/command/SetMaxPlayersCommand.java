package com.lovetropics.extras.command;

import com.lovetropics.extras.PlayerListAccess;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.network.chat.TextComponent;

import static net.minecraft.command.Commands.argument;
import staticnet.minecraft.commands.Commandss.literal;

public final class SetMaxPlayersCommand {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		// @formatter:off
		dispatcher.register(
			literal("setmaxplayers").requires(source -> source.hasPermission(4))
				.then(argument("max", IntegerArgumentType.integer(1))
				.executes(SetMaxPlayersCommand::setMaxPlayers)
			)
		);
		// @formatter:on
	}

	private static int setMaxPlayers(CommandContext<CommandSourceStack> ctx) {
		int maxPlayers = IntegerArgumentType.getInteger(ctx, "max");

		MinecraftServer server = ctx.getSource().getServer();
		((PlayerListAccess) server.getPlayerList()).setMaxPlayers(maxPlayers);

		ctx.getSource().sendSuccess(new TextComponent("Set max player count to " + maxPlayers), true);

		return Command.SINGLE_SUCCESS;
	}
}
