package com.lovetropics.extras.collectible;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.lovetropics.extras.ExtraDataComponents;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import com.sun.jdi.connect.Connector;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.server.command.EnumArgument;

import java.util.Collections;
import java.util.Optional;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class GenerateCollectibleCommand {
    private static final SimpleCommandExceptionType ALREADY_CREATED = new SimpleCommandExceptionType(Component.literal("Collectible data already created please use /generatecollectible modify"));
    private static final SimpleCommandExceptionType NOT_CREATED = new SimpleCommandExceptionType(Component.literal("Collectible data not found on held item. Please use \"/generatecollectible create\" first."));
    private static final SimpleCommandExceptionType NOT_A_COLLECTIBLE = new SimpleCommandExceptionType(Component.literal("The held item is not a collectible."));

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
        // @formatter:off
        dispatcher.register(literal("generatecollectible")
                .requires(source -> source.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(literal("create")
                        .then(argument("name", StringArgumentType.word())
                                .then(argument("rarity", EnumArgument.enumArgument(CollectibleRarity.class))
                                        .executes(context -> create(context, StringArgumentType.getString(context, "name"), context.getArgument("rarity", CollectibleRarity.class))
                                        )
                                )
                        )
                )
                .then(literal("modify")
                        .then(literal("rarity")
                                .then(argument("rarity", EnumArgument.enumArgument(CollectibleRarity.class))
                                        .executes(context -> setRarity(context, context.getArgument("rarity", CollectibleRarity.class)))
                                )
                        )
                        .then(literal("description")
                                .then(argument("description", BoolArgumentType.bool())
                                    .executes(context -> setDescription(context, context.getArgument("description", Boolean.class)))
                                )
                        )
                        .then(literal("extra_lore")
                               .then(argument("extra_lore", ComponentArgument.textComponent(buildContext))
                                   .executes(context -> setExtraLore(context, ComponentArgument.getResolvedComponent(context, "extra_lore")))
                               )
                        )
                )
                .then(literal("export")
                        .executes(GenerateCollectibleCommand::export)
                )
        );
        // @formatter:on
    }

    private static int export(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        ItemStack heldItem = player.getMainHandItem();
        CollectibleDisplayInfo collectibleLore = heldItem.get(ExtraDataComponents.COLLECTIBLE_LORE);
        if (collectibleLore == null) {
            throw NOT_A_COLLECTIBLE.create();
        }
        Collectible collectible = new Collectible(heldItem);
        JsonElement jsonElement = Collectible.DIRECT_CODEC.encodeStart(JsonOps.INSTANCE, collectible).getOrThrow();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(jsonElement);
        ctx.getSource().sendSystemMessage(Component.literal("Exported Click to Copy").withStyle(style -> style
                .withClickEvent(new ClickEvent.CopyToClipboard(json))));
        return Command.SINGLE_SUCCESS;
    }

    private static int create(CommandContext<CommandSourceStack> ctx, String name, CollectibleRarity rarity) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        ItemStack heldItem = player.getMainHandItem();
        CollectibleDisplayInfo collectibleLore = heldItem.get(ExtraDataComponents.COLLECTIBLE_LORE);
        if (collectibleLore == null) {
            CollectibleDisplayInfo newLore = new CollectibleDisplayInfo(name, false, rarity, Optional.empty());
            heldItem.set(ExtraDataComponents.COLLECTIBLE_LORE, newLore);
            heldItem.set(DataComponents.CUSTOM_NAME, Component.translatable("lt.collectible." + name + ".name")
                    .withStyle(style -> style.withColor(TextColor.parseColor(rarity.getColor()).getOrThrow())));
            ctx.getSource().sendSuccess(() -> Component.literal("Initialized Collectable " + name + " with rarity " + rarity), false);
            return Command.SINGLE_SUCCESS;
        }
        throw ALREADY_CREATED.create();
    }

    private static int setExtraLore(CommandContext<CommandSourceStack> ctx, Component component) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        ItemStack heldItem = player.getMainHandItem();
        CollectibleDisplayInfo collectibleLore = heldItem.get(ExtraDataComponents.COLLECTIBLE_LORE);
        if (collectibleLore != null) {
            CollectibleDisplayInfo newLore = new CollectibleDisplayInfo(
                    collectibleLore.name(),
                    collectibleLore.description(),
                    collectibleLore.rarity(),
                    Optional.of(component)
            );
            heldItem.set(ExtraDataComponents.COLLECTIBLE_LORE, newLore);
            ctx.getSource().sendSuccess(() -> Component.literal("Set collectible description to " + "" + " on held item."), false);
            return Command.SINGLE_SUCCESS;
        }
        throw NOT_CREATED.create();

    }

    private static int setDescription(CommandContext<CommandSourceStack> ctx, boolean description) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        ItemStack heldItem = player.getMainHandItem();
        CollectibleDisplayInfo collectibleLore = heldItem.get(ExtraDataComponents.COLLECTIBLE_LORE);
        if (collectibleLore != null) {
            CollectibleDisplayInfo newLore = new CollectibleDisplayInfo(
                    collectibleLore.name(),
                    description,
                    collectibleLore.rarity(),
                    collectibleLore.additionalLore()
            );
            heldItem.set(ExtraDataComponents.COLLECTIBLE_LORE, newLore);
            ctx.getSource().sendSuccess(() -> Component.literal("Set collectible description to " + description + " on held item."), false);
            return Command.SINGLE_SUCCESS;
        }
        throw NOT_CREATED.create();

    }

    private static int setRarity(CommandContext<CommandSourceStack> ctx, CollectibleRarity rarity) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        ItemStack heldItem = player.getMainHandItem();
        CollectibleDisplayInfo collectibleLore = heldItem.get(ExtraDataComponents.COLLECTIBLE_LORE);
        if (collectibleLore != null) {
            CollectibleDisplayInfo newLore = new CollectibleDisplayInfo(
                    collectibleLore.name(),
                    collectibleLore.description(),
                    rarity,
                    collectibleLore.additionalLore()
            );
            heldItem.set(ExtraDataComponents.COLLECTIBLE_LORE, newLore);
            ctx.getSource().sendSuccess(() -> Component.literal("Set collectible rarity to " + rarity.name() + " on held item."), false);
            return Command.SINGLE_SUCCESS;
        }
        throw NOT_CREATED.create();

    }

    private static int generate(CommandContext<CommandSourceStack> ctx, Rarity rarity, boolean description, boolean modifier) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        ItemStack heldItem = player.getMainHandItem();


        DataComponentPatch componentsPatch = heldItem.getComponentsPatch();

        return Command.SINGLE_SUCCESS;
    }

}
