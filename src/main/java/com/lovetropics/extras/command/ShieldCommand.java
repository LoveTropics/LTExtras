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
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class ShieldCommand {

    public static final double DEFAULT_RADIUS = 4.0;
    private static final String ARGUMENT_TARGET = "target";
    private static final String ARGUMENT_DISTANCE = "distance";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        // @formatter:off
        dispatcher.register(literal("shield").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(literal("list").executes(ShieldCommand::listShields))
                .then(argument(ARGUMENT_TARGET, EntityArgument.entity())
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
        Map<UUID, Double> shields = Shields.getShields();
        if (shields.isEmpty()) {
            source.sendSuccess(() -> Component.translatable("commands.shield.list.empty"), false);
            return 0;
        }

        MinecraftServer server = source.getServer();
        source.sendSuccess(() -> Component.translatable("commands.shield.list.header", shields.size()), false);
        shields.forEach((id, radius) -> {
            Entity entity = Shields.findEntity(server, id);
            Component name = entity != null ? entity.getDisplayName() : Component.literal(id.toString());
            source.sendSuccess(() -> Component.translatable("commands.shield.list.entry", name, radius), false);
        });
        return shields.size();
    }

    private static int toggleShield(CommandContext<CommandSourceStack> ctx, @Nullable Double distance) throws CommandSyntaxException {
        Entity target = EntityArgument.getEntity(ctx, ARGUMENT_TARGET);
        CommandSourceStack source = ctx.getSource();

        if (distance == null && Shields.isShielded(target)) {
            Shields.removeShield(target);
            if (target instanceof ServerPlayer serverPlayer) {
                serverPlayer.sendSystemMessage(Component.translatable("commands.shield.self.disabled"));
            }
            source.sendSuccess(() -> Component.translatable("commands.shield.disabled", target.getDisplayName()), true);
        } else {
            double radius = distance != null ? distance : DEFAULT_RADIUS;
            Shields.setShield(target, radius);
            if (target instanceof ServerPlayer serverPlayer) {
                serverPlayer.sendSystemMessage(Component.translatable("commands.shield.self.enabled"));
            }

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
