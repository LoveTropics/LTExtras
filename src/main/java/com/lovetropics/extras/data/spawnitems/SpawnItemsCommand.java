package com.lovetropics.extras.data.spawnitems;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

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
                                    var arg = context.getArgument("set", String.class);
                                    var set = SpawnItemsReloadListener.REGISTRY.get(ResourceLocation.parse(arg));
                                    ServerPlayer player = context.getSource().getPlayerOrException();

                                    if (set == null) {
                                        context.getSource().sendFailure(Component.translatable("spawnitems.unknown_set", Component.literal(arg).withStyle(ChatFormatting.GOLD)));
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    if (!set.canBeRestored() || !set.canApplyToPlayer(player)) {
                                        context.getSource().sendFailure(Component.translatable("spawnitems.set_not_restorable", Component.literal(arg).withStyle(ChatFormatting.GOLD)));
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    for (SpawnItems.Stack stack : set.items()) {
                                        if (!player.addItem(stack.build())) {
                                            player.drop(stack.build(), true, true);
                                        }
                                    }
                                    context.getSource().sendSuccess(() -> Component.translatable("spawnitems.restored_successfully"), false);

                                    return Command.SINGLE_SUCCESS;
                                }))));
    }
}
