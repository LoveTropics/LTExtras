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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public record RunFunctionClickEvent(ResourceLocation function, CompoundTag args) implements ExtraClickEvent {

    public static final MapCodec<RunFunctionClickEvent> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("function").forGetter(RunFunctionClickEvent::function),
                CompoundTag.CODEC.fieldOf("args").forGetter(RunFunctionClickEvent::args)
        ).apply(instance, RunFunctionClickEvent::new)
    );

    // Todo Fix Error Handling
    @Override
    public void handleAction(ServerPlayer serverPlayer) {
        MinecraftServer server = serverPlayer.getServer();
        CommandFunction<CommandSourceStack> commandFunction = server.getFunctions().get(function).orElseThrow();
        try {
            InstantiatedFunction<CommandSourceStack> instantiate = commandFunction.instantiate(args, server.getCommands().getDispatcher());
            CommandSourceStack commandSourceStack = serverPlayer.createCommandSourceStack();
            Commands.executeCommandInContext(commandSourceStack, executionContext -> ExecutionContext.queueInitialFunctionCall(executionContext, instantiate, commandSourceStack, CommandResultCallback.EMPTY));
        } catch (FunctionInstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public MapCodec<? extends ExtraClickEvent> getCodec() {
        return CODEC;
    }
}
