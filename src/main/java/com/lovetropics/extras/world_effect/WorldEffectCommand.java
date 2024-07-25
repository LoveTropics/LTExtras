package com.lovetropics.extras.world_effect;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.TimeArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class WorldEffectCommand {
    private static final DynamicCommandExceptionType WORLD_EFFECT_CONFIG_NOT_FOUND = new DynamicCommandExceptionType(id ->
            Component.literal("World effect does not exist with id: " + id)
    );

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("worldeffect")
                .requires(source -> source.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(literal("apply")
                        .then(effectArgument("effect")
                                .executes(context -> apply(context, getEffect(context, "effect"), Long.MAX_VALUE))
                                .then(argument("duration", TimeArgument.time())
                                        .executes(context -> apply(context, getEffect(context, "effect"), IntegerArgumentType.getInteger(context, "duration")))
                                )
                        )
                )
                .then(literal("clear")
                        .then(effectArgument("effect")
                                .executes(context -> clear(context, getEffect(context, "effect")))
                        )
                )
        );
    }

    private static int apply(CommandContext<CommandSourceStack> context, WorldEffectHolder effect, long duration) {
        MinecraftServer server = context.getSource().getServer();
        long expiresAt = duration == Long.MAX_VALUE ? Long.MAX_VALUE : server.overworld().getGameTime() + duration;
        WorldEffectManager.apply(context.getSource().getLevel(), effect, expiresAt);
        context.getSource().sendSuccess(() -> Component.literal("Applied world effect: " + effect.id()), false);
        return 1;
    }

    private static int clear(CommandContext<CommandSourceStack> context, WorldEffectHolder effect) {
        WorldEffectManager.clear(context.getSource().getLevel(), effect.id());
        context.getSource().sendSuccess(() -> Component.literal("Cleared world effect: " + effect.id()), false);
        return 1;
    }

    public static RequiredArgumentBuilder<CommandSourceStack, ResourceLocation> effectArgument(String name) {
        return argument(name, ResourceLocationArgument.id()).suggests((context, builder) -> SharedSuggestionProvider.suggestResource(
                WorldEffectConfigs.REGISTRY.stream().map(WorldEffectHolder::id),
                builder
        ));
    }

    private static WorldEffectHolder getEffect(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
        ResourceLocation id = ResourceLocationArgument.getId(context, name);
        WorldEffectHolder config = WorldEffectConfigs.REGISTRY.get(id);
        if (config == null) {
            throw WORLD_EFFECT_CONFIG_NOT_FOUND.create(id);
        }
        return config;
    }
}
