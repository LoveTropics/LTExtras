package com.lovetropics.extras.model_modifer;

import com.google.common.collect.Sets;
import com.lovetropics.extras.command.arugments.ModelModifierArgument;
import com.lovetropics.extras.data.attachment.ExtraAttachments;
import com.lovetropics.extras.registry.ExtraRegistries;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class ModelModifierCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        // @formatter:off
        dispatcher.register(
                literal("modelmodifer")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .then(literal("add")
                            .then(argument("entities", EntityArgument.entities())
                                .then(argument("modifier", ModelModifierArgument.modifier(context))
                                    .executes(ctx -> addModifier(ctx, ModelModifierArgument.getModifier(ctx, "modifier"), EntityArgument.getEntities(ctx, "entities")))
                                )
                            )
                        )
                        .then(literal("remove")
                            .then(argument("entities", EntityArgument.entities())
                                .then(argument("modifier", ModelModifierArgument.modifier(context))
                                    .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(
                                            getModifiers(EntityArgument.getEntities(ctx, "entities")), builder)
                                    )
                                    .executes(ctx -> removeModifier(ctx, ModelModifierArgument.getModifier(ctx, "modifier"), EntityArgument.getEntities(ctx, "entities")))
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

    private static int addModifier(CommandContext<CommandSourceStack> ctx, Holder<ModelModifier<?>> modifier, Collection<? extends Entity> entities)  {
        for (Entity entity : entities) {
            ModelModifierStore.addModifier(entity, modifier);
        }

        ctx.getSource().sendSuccess(() -> Component.literal("Added " + modifier.getRegisteredName() + " model modifier to " + entities.size() + " entities"), true);

        return Command.SINGLE_SUCCESS;
    }

    private static int removeModifier(CommandContext<CommandSourceStack> ctx, Holder<ModelModifier<?>> modifier, Collection<? extends Entity> entities)  {
        for (Entity entity : entities) {
            ModelModifierStore.removeModifier(entity, modifier);
            entity.syncData(ExtraAttachments.MODEL_MODIFIERS);
        }

        ctx.getSource().sendSuccess(() -> Component.literal("Removed " + modifier.getRegisteredName() + " model modifier for " + entities.size() + " entities"), true);

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
        List<String> modifiers = new ArrayList<>();

        for (Entity entity : entities) {
            List<Holder<ModelModifier<?>>> modelModifierTypes = ModelModifierStore.getOrDefault(entity).appliedModifiers();
            for (Holder<ModelModifier<?>> modelModifierType : modelModifierTypes) {
                if (!modifiers.contains(modelModifierType.getRegisteredName())) {
                    modifiers.add(modelModifierType.getRegisteredName());
                }
            }
        }

        String modifierString = String.join(", ",modifiers);
        ctx.getSource().sendSuccess(() -> Component.literal(entities.size() + " have " + modifierString + " modifers"), true);

        return Command.SINGLE_SUCCESS;
    }

    private static Collection<String> getModifiers(Collection<? extends Entity> entities) {
        Set<String> set = Sets.newHashSet();

        for (Entity entity : entities) {
            List<Holder<ModelModifier<?>>> holders = ModelModifierStore.getOrDefault(entity).appliedModifiers();
            for (Holder<ModelModifier<?>> holder : holders) {
                holder.unwrapKey().ifPresent(key -> set.add(key.identifier().toString()));
            }
            set.addAll(holders.stream().map(Holder::getRegisteredName).toList());
        }

        return set;
    }
}
