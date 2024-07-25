package com.lovetropics.extras.command;

import com.lovetropics.extras.data.poi.MapPoiManager;
import com.lovetropics.extras.data.poi.Poi;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.WorldCoordinates;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.BoolArgumentType.getBool;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;
import static net.minecraft.commands.SharedSuggestionProvider.suggest;
import static net.minecraft.commands.SharedSuggestionProvider.suggestResource;
import static net.minecraft.commands.arguments.ComponentArgument.getComponent;
import static net.minecraft.commands.arguments.ComponentArgument.textComponent;
import static net.minecraft.commands.arguments.ResourceLocationArgument.getId;
import static net.minecraft.commands.arguments.ResourceLocationArgument.id;
import static net.minecraft.commands.arguments.coordinates.BlockPosArgument.blockPos;

public class PoiCommand {

    private static final String COMMAND_BASE = "poi";
    private static final SimpleCommandExceptionType GENERAL_ERROR = new SimpleCommandExceptionType(Component.literal("General error"));
    private static final SimpleCommandExceptionType NOT_FOUND = new SimpleCommandExceptionType(Component.literal("POI not found"));

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        // @formatter:off
        dispatcher.register(literal(COMMAND_BASE)
                .requires(source -> source.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(literal("add")
                    .then(argument("name", word())
                    .then(argument("description", textComponent(context))
                    .then(argument("icon", id())
                            .executes(PoiCommand::addWithDefaults)
                    .then(argument("blockpos", blockPos())
                    .then(argument("enabled", bool())
                .executes(PoiCommand::add)
        )))))));

        dispatcher.register(literal(COMMAND_BASE)
            .requires(source -> source.hasPermission(Commands.LEVEL_GAMEMASTERS))
            .then(literal("enable")
                    .then(argument("name", word())
                            .suggests((ctx, builder) -> suggest(suggestDisabledPois(ctx), builder))
                .executes(PoiCommand::enable)
        )));

        dispatcher.register(literal(COMMAND_BASE)
            .requires(source -> source.hasPermission(Commands.LEVEL_GAMEMASTERS))
            .then(literal("disable")
                .then(argument("name", word())
                    .suggests((ctx, builder) -> suggest(suggestEnabledPois(ctx), builder))
                .executes(PoiCommand::disable)
        )));

        dispatcher.register(literal(COMMAND_BASE)
            .requires(source -> source.hasPermission(Commands.LEVEL_GAMEMASTERS))
            .then(literal("get")
                .then(argument("name", word())
                    .suggests((ctx, builder) -> suggest(suggestName(ctx), builder))
                .executes(PoiCommand::get)
        )));

        dispatcher.register(literal(COMMAND_BASE)
            .requires(source -> source.hasPermission(Commands.LEVEL_GAMEMASTERS))
            .then(literal("edit")
                    .then(argument("name", word())
                            .suggests((ctx, builder) -> suggest(suggestName(ctx), builder))
                    .then(argument("description", textComponent(context))
                            .suggests((ctx, builder) -> suggest(suggestDescription(ctx), builder))
                    .then(argument("icon", id())
                            .suggests((ctx, builder) -> suggestResource(suggestIcon(ctx), builder))
                    .then(argument("blockpos", blockPos())
                            .suggests((ctx, builder) -> suggest(suggestGlobalPos(ctx), builder)) //TODO how to suggest blockpos?
                    .then(argument("enabled", bool())
                            .suggests((ctx, builder) -> suggest(suggestEnabled(ctx), builder))
                .executes(PoiCommand::edit)
        )))))));

        dispatcher.register(literal(COMMAND_BASE)
            .requires(source -> source.hasPermission(Commands.LEVEL_GAMEMASTERS))
            .then(literal("list")
                .executes(PoiCommand::list)
        ));

        dispatcher.register(literal(COMMAND_BASE)
            .requires(source -> source.hasPermission(Commands.LEVEL_GAMEMASTERS))
            .then(literal("delete")
            .then(argument("name", word())
                .suggests((ctx, builder) -> suggest(suggestName(ctx), builder))
                .executes(PoiCommand::delete)
        )));
        // @formatter:on
    }

    private static int delete(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        MapPoiManager manager = MapPoiManager.get(ctx.getSource().getServer());
        Poi poi = manager.getPoi(getString(ctx, "name"));

        if (poi == null) {
            throw NOT_FOUND.create();
        }

        manager.remove(poi.name());
        ctx.getSource().sendSuccess(() -> Component.literal("Deleted POI " + poi.name()), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int get(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Poi poi = MapPoiManager.get(ctx.getSource().getServer())
                .getPoi(getString(ctx, "name"));

        if (poi == null) {
            throw NOT_FOUND.create();
        }

        ctx.getSource().sendSuccess(() -> Component.literal(poi.toString()), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int list(CommandContext<CommandSourceStack> ctx) {
        MapPoiManager.get(ctx.getSource().getServer())
                .getAllPois()
                .forEach(poi -> ctx.getSource().sendSuccess(() -> Component.literal(poi.toString()), false));

        return Command.SINGLE_SUCCESS;
    }

    private static int add(CommandContext<CommandSourceStack> ctx) {
        Poi newPoi = createPoiFromCtx(ctx);

        MapPoiManager.get(ctx.getSource().getServer()).add(newPoi);

        ctx.getSource().sendSuccess(() -> Component.literal("Added new POI " + newPoi.name()), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int addWithDefaults(CommandContext<CommandSourceStack> ctx) {
        String name = getString(ctx, "name");
        Component description = getComponent(ctx, "description");
        ResourceLocation icon = getId(ctx, "icon");
        GlobalPos globalPos = GlobalPos.of(ctx.getSource().getLevel().dimension(), ctx.getSource().getPlayer().getOnPos().above());
        final boolean enabled = false;
        List<UUID> faces = List.of();

        Poi newPoi = new Poi(name, description, icon, globalPos, enabled, faces);
        MapPoiManager.get(ctx.getSource().getServer()).add(newPoi);
        ctx.getSource().sendSuccess(() -> Component.literal("Added new disabled POI " + newPoi.name() + " at your current position"), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int enable(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        String name = getString(ctx, "name");
        if (!MapPoiManager.get(ctx.getSource().getServer()).enable(name)) {
            throw GENERAL_ERROR.create();
        }
        ctx.getSource().sendSuccess(() -> Component.literal("Enabled POI \"" + name + "\""), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int disable(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        String name = getString(ctx, "name");
        if (!MapPoiManager.get(ctx.getSource().getServer()).disable(name)) {
            throw GENERAL_ERROR.create();
        }
        ctx.getSource().sendSuccess(() -> Component.literal("Disabled POI \"" + name + "\""), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int edit(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Poi poi = MapPoiManager.get(ctx.getSource().getServer())
                .getPoi(getString(ctx, "name"));

        if (poi == null) {
            throw NOT_FOUND.create();
        }

        Poi updatedPoi = createPoiFromCtx(ctx);
        MapPoiManager.get(ctx.getSource().getServer()).add(updatedPoi);
        ctx.getSource().sendSuccess(() -> Component.literal("Updated POI \"" + poi.name() + "\""), false);
        return Command.SINGLE_SUCCESS;
    }

    private static Stream<String> suggestEnabledPois(CommandContext<CommandSourceStack> ctx) {
        return MapPoiManager.get(ctx.getSource().getServer()).getEnabledPois()
                .stream()
                .map(Poi::name);
    }

    private static Stream<String> suggestDisabledPois(CommandContext<CommandSourceStack> ctx) {
        return MapPoiManager.get(ctx.getSource().getServer()).getDisabledPois()
                .stream()
                .map(Poi::name);
    }

    private static Stream<String> suggestName(CommandContext<CommandSourceStack> ctx) {
        return MapPoiManager.get(ctx.getSource().getServer()).getAllPois()
                .stream()
                .map(Poi::name);
    }

    private static Stream<String> suggestDescription(CommandContext<CommandSourceStack> ctx) {
        String name = getString(ctx, "name");
        Poi poi = MapPoiManager.get(ctx.getSource().getServer()).getPoi(name);
        if (poi != null) {
            return Stream.of("\"" + poi.description() + "\"");
        }
        return Stream.empty();
    }

    private static Stream<String> suggestEnabled(CommandContext<CommandSourceStack> ctx) {
        String name = getString(ctx, "name");
        Poi poi = MapPoiManager.get(ctx.getSource().getServer()).getPoi(name);
        if (poi != null) {
            return Stream.of(String.valueOf(poi.enabled()));
        }
        return Stream.empty();
    }

    private static Poi createPoiFromCtx(CommandContext<CommandSourceStack> ctx) {
        String name = getString(ctx, "name");
        Component description = getComponent(ctx, "description");
        ResourceLocation icon = getId(ctx, "icon");
        WorldCoordinates worldCoordinates = ctx.getArgument("blockpos", WorldCoordinates.class);
        GlobalPos globalPos = GlobalPos.of(ctx.getSource().getLevel().dimension(), worldCoordinates.getBlockPos(ctx.getSource()));
        boolean enabled = getBool(ctx, "enabled");
        List<UUID> faces = List.of();

        return new Poi(name, description, icon, globalPos, enabled, faces);
    }

    private static Stream<String> suggestGlobalPos(CommandContext<CommandSourceStack> ctx) {
        String name = getString(ctx, "name");
        Poi poi = MapPoiManager.get(ctx.getSource().getServer()).getPoi(name);
        if (poi != null) {
            return Stream.of(poi.globalPos().pos().toString());
        }
        return Stream.empty();
    }

    private static Stream<ResourceLocation> suggestIcon(CommandContext<CommandSourceStack> ctx) {
        String name = getString(ctx, "name");
        Poi poi = MapPoiManager.get(ctx.getSource().getServer()).getPoi(name);
        if (poi != null) {
            return Stream.of(poi.resourceLocation());
        }
        return Stream.empty();
    }
}
