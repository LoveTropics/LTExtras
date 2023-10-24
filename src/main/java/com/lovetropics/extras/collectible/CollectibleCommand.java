package com.lovetropics.extras.collectible;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.Util;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.function.Predicate;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;
import static net.minecraft.commands.arguments.EntityArgument.getPlayers;
import static net.minecraft.commands.arguments.EntityArgument.players;
import static net.minecraft.commands.arguments.item.ItemArgument.getItem;
import static net.minecraft.commands.arguments.item.ItemArgument.item;

public class CollectibleCommand {
    private static final SimpleCommandExceptionType GAVE_TO_NO_PLAYERS = new SimpleCommandExceptionType(Component.literal("Did not find any players to give this collectible to"));
    private static final SimpleCommandExceptionType CLEARED_FROM_NO_PLAYERS = new SimpleCommandExceptionType(Component.literal("Did not find any players to remove this collectible from"));

    public static void register(final CommandDispatcher<CommandSourceStack> dispatcher, final CommandBuildContext buildContext) {
        dispatcher.register(literal("collectible")
                .requires(source -> source.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(literal("give")
                        .then(argument("target", players())
                                .then(argument("item", item(buildContext))
                                        .executes(c -> give(c, getPlayers(c, "target"), getItem(c, "item")))
                                )
                        )
                )
                .then(literal("clear")
                        .then(argument("target", players())
                                .then(argument("item", item(buildContext))
                                        .executes(c -> clear(c, getPlayers(c, "target"), getItem(c, "item")))
                                )
                                .executes(c -> clear(c, getPlayers(c, "target"), i -> true))
                        )
                )
                // Very hacky
                .then(literal("countdisguises").executes(CollectibleCommand::countDisguises))
        );
    }

    private static int give(final CommandContext<CommandSourceStack> ctx, final Collection<ServerPlayer> players, final ItemInput item) throws CommandSyntaxException {
        final ItemStack stack = item.createItemStack(1, true);
        final Collectible collectible = new Collectible(stack);

        int result = 0;
        for (final ServerPlayer player : players) {
            final CollectibleStore collectibles = CollectibleStore.getNullable(player);
            if (collectibles != null && collectibles.give(collectible)) {
                result++;
            }
        }

        if (result == 0) {
            throw GAVE_TO_NO_PLAYERS.create();
        }

        final int finalResult = result;
        ctx.getSource().sendSuccess(() -> Component.translatable("Gave %s to %s players", stack.getDisplayName(), finalResult), false);

        return result;
    }

    private static int clear(final CommandContext<CommandSourceStack> ctx, final Collection<ServerPlayer> players, final Predicate<ItemStack> itemPredicate) throws CommandSyntaxException {
        final Predicate<Collectible> predicate = collectible -> itemPredicate.test(collectible.createItemStack(Util.NIL_UUID));

        int count = 0;
        for (final ServerPlayer player : players) {
            final CollectibleStore collectibles = CollectibleStore.getNullable(player);
            if (collectibles != null && collectibles.clear(predicate)) {
                clearCollectibleItems(player.getInventory(), predicate);
                count++;
            }
        }

        if (count == 0) {
            throw CLEARED_FROM_NO_PLAYERS.create();
        }

        final int finalCount = count;
        ctx.getSource().sendSuccess(() -> Component.translatable("Cleared collectibles from %s players", finalCount), false);

        return finalCount;
    }

    private static void clearCollectibleItems(final Inventory inventory, final Predicate<Collectible> predicate) {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            final ItemStack stack = inventory.getItem(i);
            final Collectible collectible = Collectible.byItem(stack);
            if (collectible != null && predicate.test(collectible)) {
                inventory.removeItemNoUpdate(i);
            }
        }
    }

    private static final ResourceKey<Item> DISGUISE = ResourceKey.create(Registries.ITEM, new ResourceLocation("ltminigames", "disguise"));

    private static int countDisguises(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final ServerPlayer player = context.getSource().getPlayerOrException();
        final CollectibleStore collectibles = CollectibleStore.getNullable(player);
        if (collectibles == null) {
            return 0;
        }
        return collectibles.count(collectible -> {
            if (collectible.item().is(DISGUISE)) {
                return collectible.tag().isEmpty() || !collectible.tag().get().getBoolean("donation_goal");
            }
            return false;
        });
    }
}
