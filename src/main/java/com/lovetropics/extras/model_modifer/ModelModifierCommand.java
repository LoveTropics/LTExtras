package com.lovetropics.extras.model_modifer;

import com.google.common.collect.Sets;
import com.lovetropics.extras.command.arguments.ModelModifierArgumentType;
import com.lovetropics.extras.data.attachment.ExtraAttachments;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static net.minecraft.commands.Commands.literal;
import static net.minecraft.commands.Commands.argument;

public class ModelModifierCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        // @formatter:off
        dispatcher.register(
                literal("modelmodifer")
                        .requires(source -> source.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .then(literal("add")
                            .then(argument("entities", EntityArgument.entities())
                                .then(argument("modifier", ModelModifierArgumentType.modelModifier())
                                    .executes(ctx -> addModifier(ctx, ctx.getArgument("modifier", ModelModifierType.class), EntityArgument.getEntities(ctx, "entities")))
                                )
                            )
                        )
                        .then(literal("remove")
                            .then(argument("entities", EntityArgument.entities())
                                .then(argument("modifier", ModelModifierArgumentType.modelModifier())
                                    .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(
                                            getModifiers(EntityArgument.getEntities(ctx, "entities")), builder)
                                    )
                                    .executes(ctx -> removeModifier(ctx, ctx.getArgument("modifier", ModelModifierType.class), EntityArgument.getEntities(ctx, "entities")))
                                )
                            )
                        )
                        .then(literal("reset")
                            .then(argument("entities", EntityArgument.entities())
                                .executes(ctx -> resetModifiers(ctx, EntityArgument.getEntities(ctx, "entities")))
                            )
                        )
                        .then(literal("list")
                            .then(argument("entities", EntityArgument.entities())
                                .executes(ctx -> listModifiers(ctx, EntityArgument.getEntities(ctx, "entities")))
                            )
                        )
        );
    }

    private static int addModifier(CommandContext<CommandSourceStack> ctx, ModelModifierType modifier, Collection<? extends Entity> entities)  {
        for (Entity entity : entities) {
            ModelModifierStore.addModifier(entity, modifier);
        }

        ctx.getSource().sendSuccess(() -> Component.literal("Added " + modifier.getSerializedName() + " model modifier to " + entities.size() + " entities"), true);

        return Command.SINGLE_SUCCESS;
    }

    private static int removeModifier(CommandContext<CommandSourceStack> ctx, ModelModifierType modifier, Collection<? extends Entity> entities)  {
        for (Entity entity : entities) {
            ModelModifierStore.removeModifier(entity, modifier);
            entity.syncData(ExtraAttachments.MODEL_MODIFIERS);
        }

        ctx.getSource().sendSuccess(() -> Component.literal("Removed " + modifier.getSerializedName() + " model modifier for " + entities.size() + " entities"), true);

        return Command.SINGLE_SUCCESS;
    }

    private static int resetModifiers(CommandContext<CommandSourceStack> ctx, Collection<? extends Entity> entities)  {
        for (Entity entity : entities) {
            ModelModifierStore.resetModifiers(entity);
        }

        ctx.getSource().sendSuccess(() -> Component.literal("Cleared model modifiers from " + entities.size() + " entities"), true);

        return Command.SINGLE_SUCCESS;
    }

    private static int listModifiers(CommandContext<CommandSourceStack> ctx, Collection<? extends Entity> entities)  {
        List<ModelModifierType> modifiers = new ArrayList<>();

        for (Entity entity : entities) {
            List<ModelModifierType> modelModifierTypes = ModelModifierStore.getOrDefault(entity).appliedModifiers();
            for (ModelModifierType modelModifierType : modelModifierTypes) {
                if (!modifiers.contains(modelModifierType)) {
                    modifiers.add(modelModifierType);
                }
            }
        }

        String modifierString = modifiers.stream().map(ModelModifierType::getSerializedName).collect(Collectors.joining(", "));
        ctx.getSource().sendSuccess(() -> Component.literal(entities.size() + " have " + modifierString + " modifers"), true);

        return Command.SINGLE_SUCCESS;
    }

    private static Collection<String> getModifiers(Collection<? extends Entity> entities) {
        Set<String> set = Sets.newHashSet();

        for (Entity entity : entities) {
            set.addAll(ModelModifierStore.getOrDefault(entity).getFormattedModifierNames());
        }

        return set;
    }
}
