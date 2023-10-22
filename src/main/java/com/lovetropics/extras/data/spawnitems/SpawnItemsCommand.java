package com.lovetropics.extras.data.spawnitems;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class SpawnItemsCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("spawnitems")
                .then(literal("restore")
                        .then(argument("set", StringArgumentType.greedyString())
                                .suggests((context, builder) ->
                                        SharedSuggestionProvider.suggest(SpawnItemsReloadListener.REGISTRY.entrySet().stream().filter(e -> e.getValue().canBeRestored())
                                                .map(e -> e.getKey().toString()), builder))
                                .executes(context -> {
                                    final var arg = context.getArgument("set", String.class);
                                    final var set = SpawnItemsReloadListener.REGISTRY.get(new ResourceLocation(arg));

                                    if (set == null) {
                                        context.getSource().sendFailure(Component.translatable("spawnitems.unknown_set", Component.literal(arg).withStyle(ChatFormatting.GOLD)));
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    if (!set.canBeRestored()) {
                                        context.getSource().sendFailure(Component.translatable("spawnitems.set_not_restorable", Component.literal(arg).withStyle(ChatFormatting.GOLD)));
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    for (SpawnItems.Stack stack : set.items()) {
                                        if (!context.getSource().getPlayerOrException().addItem(stack.build())) {
                                            assert context.getSource().getPlayer() != null;

                                            context.getSource().getPlayer().drop(stack.build(), true, true);
                                        }
                                    }
                                    context.getSource().sendSuccess(() -> Component.translatable("spawnitems.restored_successfully"), false);

                                    return Command.SINGLE_SUCCESS;
                                }))));
    }
}
