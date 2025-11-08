package com.lovetropics.extras.collectible;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.lovetropics.extras.ExtraDataComponents;
import com.lovetropics.extras.registry.ExtraRegistries;
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
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.arguments.ResourceOrTagArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.equipment.Equippable;
import net.neoforged.neoforge.server.command.EnumArgument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class GenerateCollectibleCommand {
    private static final SimpleCommandExceptionType ALREADY_CREATED = new SimpleCommandExceptionType(Component.literal("Collectible data already created please use /generatecollectible modify"));
    private static final SimpleCommandExceptionType NOT_CREATED = new SimpleCommandExceptionType(Component.literal("Collectible data not found on held item. Please use \"/generatecollectible create\" first."));
    private static final SimpleCommandExceptionType NOT_A_COLLECTIBLE = new SimpleCommandExceptionType(Component.literal("The held item is not a collectible."));

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

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
                        .then(literal("name")
                                .then(argument("name", StringArgumentType.word())
                                        .executes(context -> setName(context, StringArgumentType.getString(context, "extra_lore")))
                                )
                        )
                )
                .then(literal("special")
                        .then(literal("entity_named")
                                .then(argument("entity", ResourceArgument.resource(buildContext, Registries.ENTITY_TYPE))
                                        .then(argument("type", EnumArgument.enumArgument(CollectibleExtraDisplayType.class))
                                                .executes(context -> setSpecialName(
                                                        context,
                                                        ResourceArgument.getResource(context, "entity", Registries.ENTITY_TYPE),
                                                        context.getArgument("type", CollectibleExtraDisplayType.class)
                                                ))
                                        )
                                )
                        )
                        .then(literal("hide_components")
                                .then(argument("components", ResourceOrTagArgument.resourceOrTag(buildContext, Registries.DATA_COMPONENT_TYPE))
                                        .executes(context -> hideComponents(context, ResourceOrTagArgument.getResourceOrTag(context, "components", Registries.DATA_COMPONENT_TYPE))
                                        )
                                )
                        )
                        .then(literal("remove_components")
                                .then(argument("components", ResourceOrTagArgument.resourceOrTag(buildContext, Registries.DATA_COMPONENT_TYPE))
                                        .executes(context -> removeComponents(context, ResourceOrTagArgument.getResourceOrTag(context, "components", Registries.DATA_COMPONENT_TYPE))
                                        )
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
        String json = GSON.toJson(Collectible.DIRECT_CODEC.encodeStart(JsonOps.INSTANCE, collectible).getOrThrow());
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
            heldItem.set(DataComponents.CUSTOM_NAME, newLore.getComponent(heldItem));
            ctx.getSource().sendSuccess(() -> Component.literal("Initialized Collectable " + name + " with rarity " + rarity), false);
            return Command.SINGLE_SUCCESS;
        }
        throw ALREADY_CREATED.create();
    }

    private static int setName(CommandContext<CommandSourceStack> ctx, String name) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        ItemStack heldItem = player.getMainHandItem();
        CollectibleDisplayInfo collectibleLore = heldItem.get(ExtraDataComponents.COLLECTIBLE_LORE);
        if (collectibleLore != null) {
            CollectibleDisplayInfo newLore = new CollectibleDisplayInfo(
                    name,
                    collectibleLore.description(),
                    collectibleLore.rarity(),
                    collectibleLore.additionalLore()
            );
            heldItem.set(ExtraDataComponents.COLLECTIBLE_LORE, newLore);
            ctx.getSource().sendSuccess(() -> Component.literal("Set collectible description to " + name + " on held item."), false);
            return Command.SINGLE_SUCCESS;
        }
        throw NOT_CREATED.create();
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
                    Optional.of(List.of(component))
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

    private static int setSpecialName(CommandContext<CommandSourceStack> context, Holder.Reference<EntityType<?>> entity, CollectibleExtraDisplayType type) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        ItemStack heldItem = player.getMainHandItem();
        CollectibleDisplayInfo collectibleLore = heldItem.get(ExtraDataComponents.COLLECTIBLE_LORE);
        if (collectibleLore != null) {
            EntityType<?> value = entity.value();
            MutableComponent append = Component.empty().append(value.getDescription())
                    .append(" ").append(type.getComponent())
                    .withStyle(style -> {
                        return style.withItalic(false).withColor(TextColor.parseColor(collectibleLore.rarity().getColor()).getOrThrow());
                    });
            heldItem.set(DataComponents.CUSTOM_NAME, append);
            Equippable equippable = heldItem.get(DataComponents.EQUIPPABLE);
            if (equippable != null) {
                List<Component> extraLore = new ArrayList<>();
                EquipmentSlot slot = equippable.slot();
                MutableComponent translatable = Component.translatable("item.modifiers." + slot.getSerializedName());
                extraLore.add(translatable.withStyle(ChatFormatting.GRAY));
                extraLore.addAll(type.getAdditionalLore(value));
                CollectibleDisplayInfo newLore = new CollectibleDisplayInfo(
                        collectibleLore.name(),
                        collectibleLore.description(),
                        collectibleLore.rarity(),
                        Optional.of(extraLore));
                heldItem.set(ExtraDataComponents.COLLECTIBLE_LORE, newLore);
                context.getSource().sendSuccess(() -> Component.literal("Set special collectible name for entity " + value.getDescription().getString() + " on held item."), false);
            }
            return Command.SINGLE_SUCCESS;
        }
        throw NOT_CREATED.create();
    }

    private static int hideComponents(CommandContext<CommandSourceStack> context, ResourceOrTagArgument.Result<DataComponentType<?>> components) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        ItemStack heldItem = player.getMainHandItem();
        List<DataComponentType<?>> typesToHide = new ArrayList<>();
        for (Holder<DataComponentType<?>> dataComponentType : BuiltInRegistries.DATA_COMPONENT_TYPE.asHolderIdMap()) {
            if (components.test(dataComponentType) && heldItem.has(dataComponentType.value())) {
                typesToHide.add(dataComponentType.value());
            }
        }
        TooltipDisplay tooltipDisplay = heldItem.has(DataComponents.TOOLTIP_DISPLAY) ? heldItem.get(DataComponents.TOOLTIP_DISPLAY) : TooltipDisplay.DEFAULT;
        for (DataComponentType<?> dataComponentType : typesToHide) {
            tooltipDisplay = tooltipDisplay.withHidden(dataComponentType, true);
        }
        heldItem.set(DataComponents.TOOLTIP_DISPLAY, tooltipDisplay);
        context.getSource().sendSuccess(() -> Component.literal("Hid specified data components on held item."), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int removeComponents(CommandContext<CommandSourceStack> context, ResourceOrTagArgument.Result<DataComponentType<?>> components) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        ItemStack heldItem = player.getMainHandItem();
        for (Holder<DataComponentType<?>> dataComponentType : BuiltInRegistries.DATA_COMPONENT_TYPE.asHolderIdMap()) {
            if (components.test(dataComponentType) && heldItem.has(dataComponentType.value())) {
                heldItem.remove(dataComponentType.value());
            }
        }
        context.getSource().sendSuccess(() -> Component.literal("Removed specified data components on held item."), true);
        return Command.SINGLE_SUCCESS;
    }

}
