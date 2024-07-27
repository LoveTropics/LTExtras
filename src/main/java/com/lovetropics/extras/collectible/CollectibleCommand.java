package com.lovetropics.extras.collectible;

import com.lovetropics.extras.ExtraTags;
import com.lovetropics.extras.mixin.ResourceKeyArgumentAccessor;
import com.lovetropics.extras.registry.ExtraRegistries;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.Util;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceOrTagArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;
import static net.minecraft.commands.arguments.EntityArgument.getPlayers;
import static net.minecraft.commands.arguments.EntityArgument.players;
import static net.minecraft.commands.arguments.ResourceKeyArgument.key;
import static net.minecraft.commands.arguments.ResourceOrTagArgument.resourceOrTag;
import static net.minecraft.commands.arguments.item.ItemArgument.getItem;
import static net.minecraft.commands.arguments.item.ItemArgument.item;
import static net.minecraft.commands.arguments.item.ItemPredicateArgument.getItemPredicate;
import static net.minecraft.commands.arguments.item.ItemPredicateArgument.itemPredicate;

public class CollectibleCommand {
    private static final SimpleCommandExceptionType GAVE_TO_NO_PLAYERS = new SimpleCommandExceptionType(Component.literal("Did not find any players to give this collectible to"));
    private static final SimpleCommandExceptionType CLEARED_FROM_NO_PLAYERS = new SimpleCommandExceptionType(Component.literal("Did not find any players to remove this collectible from"));
    private static final DynamicCommandExceptionType ERROR_INVALID_COLLECTIBLE = new DynamicCommandExceptionType(name -> Component.translatableEscape("There is no collectible with id: '%s'", name));

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
        dispatcher.register(literal("collectible")
                .requires(source -> source.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(literal("give")
                        .then(argument("target", players())
                                .then(literal("item")
                                        .then(argument("item", item(buildContext))
                                                .executes(c -> give(c, getPlayers(c, "target"), getItem(c, "item")))
                                        )
                                )
                                .then(argument("collectible", key(ExtraRegistries.COLLECTIBLE))
                                        .executes(c -> give(c, getPlayers(c, "target"), getCollectible(c, "collectible")))
                                )
                        )
                )
                .then(literal("clear")
                        .then(argument("target", players())
                                .then(literal("item")
                                        .then(argument("item", itemPredicate(buildContext))
                                                .executes(c -> clear(c, getPlayers(c, "target"), itemToCollectiblePredicate(getItemPredicate(c, "item"))))
                                        )
                                )
                                .then(argument("collectible", resourceOrTag(buildContext, ExtraRegistries.COLLECTIBLE))
                                        .executes(c -> clear(c, getPlayers(c, "target"), ResourceOrTagArgument.getResourceOrTag(c, "collectible", ExtraRegistries.COLLECTIBLE)))
                                )
                                .executes(c -> clear(c, getPlayers(c, "target"), i -> true))
                        )
                )
                .then(literal("lock").then(argument("target", players()).executes(context -> setLocked(context, true))))
                .then(literal("unlock").then(argument("target", players()).executes(context -> setLocked(context, false))))
                // Very hacky
                .then(literal("countdisguises").executes(CollectibleCommand::countDisguises))
                .then(literal("find")
                        .executes(context -> findCollectibles(context.getSource(), stack -> true))
                        .then(literal("item")
                                .then(argument("item", itemPredicate(buildContext))
                                        .executes(context -> findCollectibles(context.getSource(), getItemPredicate(context, "item")))
                                )
                        )
                )
        );
    }

    private static Holder.Reference<Collectible> getCollectible(CommandContext<CommandSourceStack> context, String argument) throws CommandSyntaxException {
        return ResourceKeyArgumentAccessor.callResolveKey(context, argument, ExtraRegistries.COLLECTIBLE, ERROR_INVALID_COLLECTIBLE);
    }

    private static int give(CommandContext<CommandSourceStack> ctx, Collection<ServerPlayer> players, ItemInput item) throws CommandSyntaxException {
        ItemStack stack = item.createItemStack(1, true);
        return give(ctx, players, Holder.direct(new Collectible(stack)));
    }

    private static int give(CommandContext<CommandSourceStack> ctx, Collection<ServerPlayer> players, Holder<Collectible> collectible) throws CommandSyntaxException {
        int result = 0;
        for (ServerPlayer player : players) {
            CollectibleStore collectibles = CollectibleStore.get(player);
            if (collectibles.give(collectible)) {
                result++;
            }
        }

        if (result == 0) {
            throw GAVE_TO_NO_PLAYERS.create();
        }

        int finalResult = result;
        ItemStack stack = Collectible.createItemStack(collectible, Util.NIL_UUID);
        ctx.getSource().sendSuccess(() -> Component.translatable("Gave %s to %s players", stack.getDisplayName(), finalResult), false);

        return result;
    }

    private static Predicate<Holder<Collectible>> itemToCollectiblePredicate(Predicate<ItemStack> itemPredicate) {
        return collectible -> itemPredicate.test(Collectible.createItemStack(collectible, Util.NIL_UUID));
    }

    private static int clear(CommandContext<CommandSourceStack> ctx, Collection<ServerPlayer> players, Predicate<Holder<Collectible>> predicate) throws CommandSyntaxException {
        int count = 0;
        for (ServerPlayer player : players) {
            CollectibleStore collectibles = CollectibleStore.get(player);
            if (collectibles.clear(predicate)) {
                clearCollectibleItems(player.getInventory(), predicate);
                count++;
            }
        }

        if (count == 0) {
            throw CLEARED_FROM_NO_PLAYERS.create();
        }

        int finalCount = count;
        ctx.getSource().sendSuccess(() -> Component.translatable("Cleared collectibles from %s players", finalCount), false);

        return finalCount;
    }

    private static void clearCollectibleItems(Inventory inventory, Predicate<Holder<Collectible>> predicate) {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            Holder<Collectible> collectible = Collectible.byItem(stack);
            if (collectible != null && predicate.test(collectible)) {
                inventory.removeItemNoUpdate(i);
            }
        }
    }

    private static final ResourceKey<Item> DISGUISE = ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("ltminigames", "disguise"));

    private static int countDisguises(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        CollectibleStore collectibles = CollectibleStore.get(player);
        return collectibles.count(collectible ->
                collectible.value().item().is(DISGUISE) && !collectible.is(ExtraTags.Collectibles.DONATION_GOAL)
        );
    }

    private static int setLocked(CommandContext<CommandSourceStack> context, boolean locked) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        CollectibleStore store = CollectibleStore.get(player);
        store.setLocked(locked);
        return 1;
    }

    private static int findCollectibles(CommandSourceStack source, Predicate<ItemStack> item) {
        MinecraftServer server = source.getServer();
        GameProfileCache profileCache = server.getProfileCache();
        CollectibleLister.listPlayersWithItem(server, item)
                .thenApplyAsync(
                        profileIds -> profileIds.stream().map(profileCache::get).flatMap(Optional::stream).toList(),
                        Util.backgroundExecutor()
                )
                .thenAcceptAsync(profiles -> {
                    if (profiles.isEmpty()) {
                        source.sendSuccess(() -> Component.literal("Found no players"), false);
                    } else {
                        String names = profiles.stream().map(GameProfile::getName).collect(Collectors.joining(", "));
                        source.sendSuccess(() -> Component.literal("Found " + profiles.size() + " players: " + names), false);
                    }
                }, server);
        return 1;
    }
}
