package com.lovetropics.extras.command;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class TpCommand {

    private static final String ARGUMENT_NAME = "name";
    private static final SimpleCommandExceptionType PLAYER_NOT_FOUND = new SimpleCommandExceptionType(Component.translatable("commands.tpa.player_not_found"));
    private static final SimpleCommandExceptionType REQUEST_NOT_FOUND = new SimpleCommandExceptionType(Component.translatable("commands.tpa.request_not_found"));
    private static final SimpleCommandExceptionType GENERAL_ERROR = new SimpleCommandExceptionType(Component.translatable("commands.tpa.general_error"));
    private static final SimpleCommandExceptionType TOO_MUCH = new SimpleCommandExceptionType(Component.translatable("commands.tpa.too_much"));
    private static final SimpleCommandExceptionType ALREADY_PENDING = new SimpleCommandExceptionType(Component.translatable("commands.tpa.pending_request_exists"));
    private static final SimpleCommandExceptionType NO_SELF_TELEPORT = new SimpleCommandExceptionType(Component.translatable("commands.tpa.no_self_teleport"));

    private static final int REQUEST_CACHE_SIZE = 50;
    private static final Duration REQUEST_TIMEOUT_DURATION = Duration.ofMinutes(1);

    //requester, target
    private static final Cache<UUID, UUID> requestCache = CacheBuilder.newBuilder()
            .maximumSize(REQUEST_CACHE_SIZE)
            .expireAfterWrite(REQUEST_TIMEOUT_DURATION)
            .build();

    //requester, original position
    private static final Cache<UUID, GlobalPos> backCache = CacheBuilder.newBuilder()
            .maximumSize(REQUEST_CACHE_SIZE)
            .expireAfterWrite(REQUEST_TIMEOUT_DURATION)
            .build();

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        // @formatter:off
		dispatcher.register(literal("tpa")
				.then(argument(ARGUMENT_NAME, EntityArgument.player())
						.executes(TpCommand::tpRequest)
		));

		dispatcher.register(literal("tpaccept")
				.then(argument(ARGUMENT_NAME, EntityArgument.player())
						.executes(TpCommand::tpAccept))
                .then(literal("all")
                        .executes(TpCommand::acceptAllTpRequests))
        );

        dispatcher.register(literal("back")
                        .executes(TpCommand::tpBack)
        );

        dispatcher.register(literal("tphelp")
                .executes(TpCommand::tpHelp)
        );
        // @formatter:on
    }

    private static int tpHelp(final CommandContext<CommandSourceStack> ctx) {
        ctx.getSource().sendSuccess(() -> Component.translatable("commands.tpa.help.tpa"), false);
        ctx.getSource().sendSuccess(() -> Component.translatable("commands.tpa.help.tpaccept"), false);
        ctx.getSource().sendSuccess(() -> Component.translatable("commands.tpa.help.back"), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int tpBack(final CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        final GlobalPos globalPos = backCache.getIfPresent(ctx.getSource().getPlayerOrException().getUUID());
        if (globalPos == null) {
            throw GENERAL_ERROR.create();
        }

        final ServerPlayer executingPlayer = ctx.getSource().getPlayerOrException();
        final GlobalPos newBackPos = GlobalPos.of(executingPlayer.level().dimension(), executingPlayer.blockPosition());
        backCache.put(executingPlayer.getUUID(), newBackPos); //This allows going back and forth using /back

        doTeleport(executingPlayer, globalPos);
        return Command.SINGLE_SUCCESS;
    }

    private static int tpAccept(final CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        final ServerPlayer tpRequester = EntityArgument.getPlayer(ctx, ARGUMENT_NAME);
        final UUID requestedTarget = requestCache.getIfPresent(tpRequester.getUUID());

        final UUID commandExecutor = ctx.getSource().getPlayerOrException().getUUID();

        if (requestedTarget == null || !requestedTarget.equals(commandExecutor)) {
            throw REQUEST_NOT_FOUND.create();
        }

        final ServerPlayer executingPlayer = ctx.getSource().getPlayerOrException();
        teleportAndSendMessage(tpRequester, executingPlayer);

        return Command.SINGLE_SUCCESS;
    }

    private static int acceptAllTpRequests(final CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        final ServerPlayer executingPlayer = ctx.getSource().getPlayerOrException();

        final Map<UUID, UUID> requesterTargetMap = requestCache.asMap().entrySet().stream()
                .filter(entry -> entry.getValue().equals(executingPlayer.getUUID()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        for (Map.Entry<UUID, UUID> e : requesterTargetMap.entrySet()) {
            ServerPlayer playerToTeleport = nonNullPlayerByUUID(ctx, e.getKey());
            teleportAndSendMessage(playerToTeleport, executingPlayer);
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int tpRequest(final CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        final ServerPlayer requestingPlayer = ctx.getSource().getPlayerOrException();
        final ServerPlayer targetPlayer = EntityArgument.getPlayer(ctx, ARGUMENT_NAME);

        if (requestingPlayer.getUUID().equals(targetPlayer.getUUID())) {
            throw NO_SELF_TELEPORT.create();
        }

        spamCheck(requestingPlayer.getUUID());

        final Style style = Style.EMPTY.withColor(ChatFormatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept " + requestingPlayer.getGameProfile().getName()));
        final MutableComponent hereComponent = Component.translatable("commands.tpa.here").withStyle(style);
        final MutableComponent translatable = Component.translatable("commands.tpa.request", requestingPlayer.getName(), hereComponent, requestingPlayer.getName());
        targetPlayer.sendSystemMessage(translatable);

        requestingPlayer.sendSystemMessage(Component.translatable("commands.tpa.request_sent"));
        requestCache.put(requestingPlayer.getUUID(), targetPlayer.getUUID());

        return Command.SINGLE_SUCCESS;
    }

    private static void teleportAndSendMessage(ServerPlayer player, ServerPlayer target) throws CommandSyntaxException {
        requestCache.invalidate(player.getUUID());
        final GlobalPos globalPos = GlobalPos.of(player.level().dimension(), player.blockPosition());
        backCache.put(player.getUUID(), globalPos);

        player.sendSystemMessage(Component.translatable("commands.tpa.tp_accepted", target.getName().getString()));

        doTeleport(player, GlobalPos.of(target.level().dimension(), target.blockPosition()));
    }

    private static void doTeleport(ServerPlayer player, GlobalPos globalPos) {
        final ServerLevel level = player.getServer().getLevel(globalPos.dimension());
        player.teleportTo(level, globalPos.pos().getX(), globalPos.pos().getY(), globalPos.pos().getZ(), player.getYRot(), player.getXRot());
    }

    private static void spamCheck(UUID requester) throws CommandSyntaxException {
        if (requestCache.getIfPresent(requester) != null) {
            throw ALREADY_PENDING.create();
        }
    }

    private static ServerPlayer nonNullPlayerByUUID(final CommandContext<CommandSourceStack> ctx, UUID uuid) throws CommandSyntaxException {
        final ServerPlayer player = ctx.getSource().getServer().getPlayerList().getPlayer(uuid);
        if (player == null) {
            throw PLAYER_NOT_FOUND.create();
        }
        return player;
    }

    public static void addTranslations(final RegistrateLangProvider provider) {
        provider.add("commands.tpa.request", "%s wants to teleport to you. Click %s (/tpaccept %s) to accept");
        provider.add("commands.tpa.here", "[here]");
        provider.add("commands.tpa.tp_accepted", "%s has accepted your teleport request. Use /back to return");
        provider.add("commands.tpa.too_much", "You are doing that too much");
        provider.add("commands.tpa.request_sent", "Request sent");
        provider.add("commands.tpa.player_not_found", "Player not found");
        provider.add("commands.tpa.request_not_found", "No teleport request");
        provider.add("commands.tpa.general_error", "Unable to teleport");
        provider.add("commands.tpa.help.tpa", "/tpa <player> - Request to teleport to a player");
        provider.add("commands.tpa.help.tpaccept", "/tpaccept <player> - Accept a teleport request from a player. Use \"all\" to accept all pending requests");
        provider.add("commands.tpa.help.back", "/back - Teleport back to where you were before teleporting");
        provider.add("commands.tpa.pending_request_exists", "A request is still pending");
        provider.add("commands.tpa.no_self_teleport", "You can't teleport to yourself");
    }
}
