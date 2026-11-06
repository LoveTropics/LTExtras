package com.lovetropics.extras.command;

import com.lovetropics.extras.data.TropiCoinsStore;
import com.lovetropics.extras.data.attachment.ExtraAttachments;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class TropiCoinsCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        // @formatter:off
        dispatcher.register(
            literal("tropicoins").requires(Commands.hasPermission(Commands.LEVEL_OWNERS))
               .then(literal("set")
                   .then(argument("players", EntityArgument.players())
                       .then(argument("amount", IntegerArgumentType.integer())
                           .executes(ctx -> TropiCoinsCommand.setTropiCoins(ctx, EntityArgument.getPlayers(ctx, "players"), IntegerArgumentType.getInteger(ctx, "amount")))
                        )
                   )
               )
               .then(literal("add")
                       .then(argument("players", EntityArgument.players())
                               .then(argument("amount", IntegerArgumentType.integer())
                       .executes(ctx -> TropiCoinsCommand.addTropiCoins(ctx, EntityArgument.getPlayers(ctx, "players"), IntegerArgumentType.getInteger(ctx, "amount")))
                   )
               )
               )
               .then(literal("get")
                   .then(argument("player", EntityArgument.player())
                       .executes(ctx -> TropiCoinsCommand.getTropiCoins(ctx, EntityArgument.getPlayer(ctx, "player")))
                   )
               )
        );
        // @formatter:on
    }

    private static int setTropiCoins(CommandContext<CommandSourceStack> ctx, Collection<ServerPlayer> players, int amount) {
        for (ServerPlayer player : players) {
            player.getData(ExtraAttachments.TROPICOINS_STORE.get()).setAmount(amount);
            player.syncData(ExtraAttachments.TROPICOINS_STORE.get());
        }
        ctx.getSource().sendSuccess(() -> Component.literal("Set TropiCoins to " + amount + " for " + players.size() + " player(s)"), true);
        return players.size();
    }

    public static int addTropiCoins(CommandContext<CommandSourceStack> ctx, Collection<ServerPlayer> players, int amount) {
        for (ServerPlayer player : players) {
            TropiCoinsStore store = player.getData(ExtraAttachments.TROPICOINS_STORE.get());
            store.setAmount(store.getAmount() + amount);
            player.syncData(ExtraAttachments.TROPICOINS_STORE.get());
        }
        ctx.getSource().sendSuccess(() -> Component.literal("Added " + amount + " TropiCoins for " + players.size() + " player(s)"), true);
        return players.size();
    }

    public static int getTropiCoins(CommandContext<CommandSourceStack> ctx, ServerPlayer player) {
        TropiCoinsStore store = player.getData(ExtraAttachments.TROPICOINS_STORE.get());
        ctx.getSource().sendSuccess(() ->
                player.getDisplayName().copy().append(Component.literal(" has " + store.getAmount() + " TropiCoins")), false);
        return store.getAmount();
    }
}
