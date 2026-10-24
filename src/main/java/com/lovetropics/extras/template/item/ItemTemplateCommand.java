package com.lovetropics.extras.template.item;

import com.lovetropics.extras.data.Named;
import com.lovetropics.extras.template.TemplateConfigs;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;

import java.util.Collection;

/**
 * LTExtras
 * FIRE EMOJI, FIRE EMOJI, FIRE EMOJI
 */
public class ItemTemplateCommand {

    private static final DynamicCommandExceptionType ENTRY_NOT_FOUND = new DynamicCommandExceptionType(type -> Component.literal("Item template not found: " + type));

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("template")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.literal("give")
                    .then(Commands.argument("target", EntityArgument.players())
                        .then(Commands.argument("template", IdentifierArgument.id())
                                .suggests((_, builder) -> SharedSuggestionProvider.suggestResource(TemplateConfigs.ITEM_TEMPLATES.keySet(), builder))
                                .executes(ItemTemplateCommand::giveStack)
                        )
                        .then(Commands.literal("*")
                                .executes(c -> giveAll(c, EntityArgument.getPlayers(c, "target")))
                        )
                    )
                )
        );
    }

    private static int giveStack(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Collection<ServerPlayer> players = EntityArgument.getPlayers(ctx, "target");
        Identifier id = IdentifierArgument.getId(ctx, "template");
        Named<ItemTemplate> entity = TemplateConfigs.ITEM_TEMPLATES.get(id);
        if (entity == null) {
            throw ENTRY_NOT_FOUND.create(id);
        }

        for (ServerPlayer player : players) {
            player.addItem(entity.value().createItemStack(player));
        }

        ctx.getSource().sendSuccess(() -> Component.literal("Gave collectible to " + players.size() + " players"), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int giveAll(CommandContext<CommandSourceStack> ctx, Collection<ServerPlayer> players) {
        for (ServerPlayer player : players) {
            Inventory inventory = player.getInventory();
            for (Named<ItemTemplate> templateHolder : TemplateConfigs.ITEM_TEMPLATES) {
                inventory.add(templateHolder.value().createItemStack(player));
            }
        }
        ctx.getSource().sendSuccess(() -> Component.literal("Gave all item templates to " + players.size() + " players"), true);
        return Command.SINGLE_SUCCESS;
    }
}
