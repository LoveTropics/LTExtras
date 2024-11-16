package com.lovetropics.extras.command;

import com.lovetropics.extras.data.poi.MapManager;
import com.lovetropics.extras.registry.ExtraRegistries;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;

import java.util.stream.Stream;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;
import static net.minecraft.commands.SharedSuggestionProvider.suggest;
import static net.minecraft.commands.SharedSuggestionProvider.suggestResource;
import static net.minecraft.commands.arguments.ResourceLocationArgument.getId;
import static net.minecraft.commands.arguments.ResourceLocationArgument.id;

public class PoiCommand {

    private static final String COMMAND_BASE = "poi";
    private static final SimpleCommandExceptionType GENERAL_ERROR = new SimpleCommandExceptionType(Component.literal("General error"));

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        // @formatter:off
        dispatcher.register(literal(COMMAND_BASE)
            .requires(source -> source.hasPermission(Commands.LEVEL_GAMEMASTERS))
            .then(literal("enable")
                    .then(argument("id", id())
                            .suggests((ctx, builder) -> suggestResource(suggestDisabledPois(ctx), builder))
                .executes(PoiCommand::enable)
        )));

        dispatcher.register(literal(COMMAND_BASE)
            .requires(source -> source.hasPermission(Commands.LEVEL_GAMEMASTERS))
            .then(literal("disable")
                .then(argument("id", id())
                    .suggests((ctx, builder) -> suggestResource(suggestEnabledPois(ctx), builder))
                .executes(PoiCommand::disable)
        )));

        // @formatter:on
    }

    private static int enable(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ResourceLocation id = getId(ctx, "id");
        MinecraftServer server = ctx.getSource().getServer();
        if (!MapManager.get(server).enable(server, ResourceKey.create(ExtraRegistries.POI, id))) {
            throw GENERAL_ERROR.create();
        }
        ctx.getSource().sendSuccess(() -> Component.literal("Enabled POI \"" + id + "\""), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int disable(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ResourceLocation id = getId(ctx, "id");
        MinecraftServer server = ctx.getSource().getServer();
        if (!MapManager.get(server).disable(server, ResourceKey.create(ExtraRegistries.POI, id))) {
            throw GENERAL_ERROR.create();
        }
        ctx.getSource().sendSuccess(() -> Component.literal("Disabled POI \"" + id + "\""), false);
        return Command.SINGLE_SUCCESS;
    }

    private static Stream<ResourceLocation> suggestEnabledPois(CommandContext<CommandSourceStack> ctx) {
        return MapManager.get(ctx.getSource().getServer()).getEnabledPois().map(ResourceKey::location);
    }

    private static Stream<ResourceLocation> suggestDisabledPois(CommandContext<CommandSourceStack> ctx) {
        return MapManager.get(ctx.getSource().getServer()).getDisabledPois().map(ResourceKey::location);
    }
}
