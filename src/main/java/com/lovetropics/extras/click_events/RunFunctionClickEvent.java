package com.lovetropics.extras.click_events;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.commands.CommandResultCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.FunctionInstantiationException;
import net.minecraft.commands.execution.ExecutionContext;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.commands.functions.InstantiatedFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Consumer;

public record RunFunctionClickEvent(Identifier function) implements ExtraClickEvent {

    public static final MapCodec<RunFunctionClickEvent> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Identifier.CODEC.fieldOf("function").forGetter(RunFunctionClickEvent::function)
        ).apply(instance, RunFunctionClickEvent::new)
    );

    @Override
    public void handleAction(ServerPlayer serverPlayer, Tag tag, Consumer<Component> errorHandler) {
        MinecraftServer server = serverPlayer.level().getServer();
        CommandFunction<CommandSourceStack> commandFunction = server.getFunctions().get(function).orElseThrow();
        try {
            CompoundTag compound = tag.asCompound().orElse(new CompoundTag());
            InstantiatedFunction<CommandSourceStack> instantiate = commandFunction.instantiate(compound, server.getCommands().getDispatcher());
            CommandSourceStack commandSourceStack = serverPlayer.createCommandSourceStack();
            Commands.executeCommandInContext(commandSourceStack, executionContext -> ExecutionContext.queueInitialFunctionCall(executionContext, instantiate, commandSourceStack, CommandResultCallback.EMPTY));
        } catch (FunctionInstantiationException e) {
            errorHandler.accept(Component.literal(e.getMessage()));
        }
    }

    @Override
    public MapCodec<? extends ExtraClickEvent> getCodec() {
        return CODEC;
    }
}
