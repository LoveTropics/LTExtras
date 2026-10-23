package com.lovetropics.extras.templates;

import com.lovetropics.extras.collectible.Collectible;
import com.lovetropics.extras.collectible.CollectibleStore;
import com.lovetropics.extras.registry.ExtraRegistries;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.logging.LogUtils;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;

import java.util.Collection;

/**
 * LTExtras
 * FIRE EMOJI, FIRE EMOJI, FIRE EMOJI
 */
public class ItemTemplateCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContex) {
        dispatcher.register(
            Commands.literal("template")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.literal("give")
                    .then(Commands.argument("target", EntityArgument.players())
                        .then(Commands.argument("template", ResourceArgument.resource(buildContex, ExtraRegistries.ITEM_TEMPLATE))
                                .executes(c -> giveStack(c, EntityArgument.getPlayers(c, "target"), ResourceArgument.getResource(c, "template", ExtraRegistries.ITEM_TEMPLATE)))
                        )
                        .then(Commands.literal("*")
                                .executes(c -> giveAll(c, EntityArgument.getPlayers(c, "target")))
                        )
                    )
                )
        );
    }

    private static int giveStack(CommandContext<CommandSourceStack> ctx, Collection<ServerPlayer> players, Holder<ItemTemplate> itemTemplateHolder) {
        ItemStack itemStack = itemTemplateHolder.value().createItemStack();
        players.forEach(player -> player.getInventory().add(itemStack));
        ctx.getSource().sendSuccess(() -> Component.literal("Gave collectible to " + players.size() + " players"), true);
        return Command.SINGLE_SUCCESS;
    }

    private static final Logger LOGGER = LogUtils.getLogger();

    private static int giveAll(CommandContext<CommandSourceStack> ctx, Collection<ServerPlayer> players) {
        RegistryAccess.Frozen registryAccess = ctx.getSource().getServer().registryAccess();
        Registry<ItemTemplate> registry = registryAccess.lookupOrThrow(ExtraRegistries.ITEM_TEMPLATE);

        for (ServerPlayer player : players) {
            Inventory inventory = player.getInventory();
            for (Holder<ItemTemplate> templateHolder : registry.asHolderIdMap()) {
                LOGGER.info("Added ItemTemplate: {}", templateHolder.getRegisteredName());
                inventory.add(templateHolder.value().createItemStack());
            }
        }
        ctx.getSource().sendSuccess(() -> Component.literal("Gave all item templates to " + players.size() + " players"), true);
        return Command.SINGLE_SUCCESS;
    }
}
