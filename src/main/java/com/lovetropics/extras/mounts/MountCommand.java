package com.lovetropics.extras.mounts;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.commands.SummonCommand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class MountCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        // @formatter:off
        dispatcher.register(literal("mount")
                .then(argument("player", EntityArgument.player())
                        .then(argument("entity", ResourceArgument.resource(context, Registries.ENTITY_TYPE))
                                .executes(ctx -> summonMount(ctx.getSource(), ResourceArgument.getSummonableEntityType(ctx, "entity"), new CompoundTag()))
                                .then(argument("nbt", CompoundTagArgument.compoundTag())
                                        .executes(ctx -> summonMount(ctx.getSource(), ResourceArgument.getSummonableEntityType(ctx, "entity"), CompoundTagArgument.getCompoundTag(ctx, "nbt")))
                                )
                        )
                )
        );
        // @formatter:on
    }

    private static int summonMount(CommandSourceStack source, Holder.Reference<EntityType<?>> entity, CompoundTag nbt) throws CommandSyntaxException {
        Entity commandEntity = source.getEntityOrException();
        Entity spawnEntity = SummonCommand.createEntity(source, entity, source.getPosition(), nbt, false);
        commandEntity.startRiding(spawnEntity, true, false);
        spawnEntity.addTag(ExtraMountController.KILL_DISMOUNT);
        return Command.SINGLE_SUCCESS;
    }
}
