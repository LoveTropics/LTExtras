package com.lovetropics.extras.command;

import com.lovetropics.extras.shield.Shields;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jspecify.annotations.Nullable;

import java.util.List;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class ShieldCommand {

    private static final String ARGUMENT_TARGET = "target";
    private static final String ARGUMENT_DISTANCE = "distance";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        // @formatter:off
        dispatcher.register(literal("shield").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(literal("list").executes(ShieldCommand::listShields))
                .then(argument(ARGUMENT_TARGET, EntityArgument.player())
                        .executes(ctx -> toggleShield(ctx, null))
                        .then(argument(ARGUMENT_DISTANCE, DoubleArgumentType.doubleArg(1.0, 32.0))
                                .executes(ctx -> toggleShield(ctx, DoubleArgumentType.getDouble(ctx, ARGUMENT_DISTANCE)))
                        )
                )
        );
        // @formatter:on
    }

    private static int listShields(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        List<ServerPlayer> shielded = Shields.getShieldedPlayers(source.getServer());
        if (shielded.isEmpty()) {
            source.sendSuccess(() -> Component.translatable("commands.shield.list.empty"), false);
            return 0;
        }

        source.sendSuccess(() -> Component.translatable("commands.shield.list.header", shielded.size()), false);
        for (ServerPlayer player : shielded) {
            source.sendSuccess(() -> Component.translatable("commands.shield.list.entry", player.getDisplayName(), Shields.getRadius(player)), false);
        }
        return shielded.size();
    }

    private static int toggleShield(CommandContext<CommandSourceStack> ctx, @Nullable Double distance) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(ctx, ARGUMENT_TARGET);
        CommandSourceStack source = ctx.getSource();

        if (distance == null && Shields.isShielded(target)) {
            Shields.removeShield(target);
            target.sendSystemMessage(Component.translatable("commands.shield.self.disabled"));
            source.sendSuccess(() -> Component.translatable("commands.shield.disabled", target.getDisplayName()), true);
        } else {
            double radius = distance != null ? distance : Shields.DEFAULT_RADIUS;
            Shields.setShield(target, radius);
            target.sendSystemMessage(Component.translatable("commands.shield.self.enabled"));

            source.sendSuccess(() -> Component.translatable("commands.shield.enabled", target.getDisplayName(), radius), true);
        }

        return Command.SINGLE_SUCCESS;
    }

    public static void addTranslations(RegistrateLangProvider provider) {
        provider.add("commands.shield.enabled", "Enabled shields on %s with %s radius");
        provider.add("commands.shield.disabled", "Disabled shields on %s");
        provider.add("commands.shield.self.enabled", "You feel a force field surrounding you");
        provider.add("commands.shield.self.disabled", "The force field dissipates");

        provider.add("commands.shield.list.empty", "No active shields");
        provider.add("commands.shield.list.header", "%s active shield(s):");
        provider.add("commands.shield.list.entry", " - %s: %s radius");
    }
}
