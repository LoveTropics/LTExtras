package com.lovetropics.extras.mounts;

import com.lovetropics.extras.data.Named;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.commands.SummonCommand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class MountCommand {

    private static final String ID_ARGUMENT = "id";
    private static final DynamicCommandExceptionType ENTITY_NOT_FOUND = new DynamicCommandExceptionType(type -> Component.translatable("commands.mount.entity_not_found", "%s"));

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        // @formatter:off
        dispatcher.register(literal("mount")
            .then(argument(ID_ARGUMENT, IdentifierArgument.id())
                .suggests((_, builder) ->
                    SharedSuggestionProvider.suggestResource(MountConfigs.MOUNTS.keySet(), builder))
                .executes(MountCommand::summonDefinedMount)
            )
        );
        // @formatter:on
    }

    private static int summonDefinedMount(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        Entity commandEntity = source.getEntityOrException();
        Identifier id = IdentifierArgument.getId(context, ID_ARGUMENT);
        Named<Mount> entity = MountConfigs.MOUNTS.get(id);
        if (entity == null) {
            throw ENTITY_NOT_FOUND.create(id);
        }

        if (entity.value().spawnFor(commandEntity, source.getLevel())) {
            return Command.SINGLE_SUCCESS;
        }

        return 0;
    }

    public static void addTranslations(RegistrateLangProvider provider) {
        provider.add("commands.mount.entity_not_found", "Mount not found in registry: %s");
    }
}
