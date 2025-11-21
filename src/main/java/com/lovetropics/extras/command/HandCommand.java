package com.lovetropics.extras.command;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.JsonOps;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import static net.minecraft.commands.Commands.literal;

public class HandCommand {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        // @formatter:off
        dispatcher.register(
                literal("hand").requires(source -> source.hasPermission(4))
                                .then(literal("json")
                                        .executes(HandCommand::json)
                                )
                                .then(literal("nbt")
                                        .executes(HandCommand::displayItem))
                        );
        // @formatter:on
    }

    private static int json(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        ItemStack heldItem = player.getMainHandItem();
        RegistryOps<JsonElement> registryops = player.getServer().registryAccess().createSerializationContext(JsonOps.INSTANCE);
        String json = GSON.toJson(ItemStack.CODEC.encodeStart(registryops, heldItem).getOrThrow());
        ctx.getSource().sendSystemMessage(Component.literal("Exported Click to Copy").withStyle(style -> style
                .withClickEvent(new ClickEvent.CopyToClipboard(json))));
        return Command.SINGLE_SUCCESS;
    }

    private static int displayItem(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Player player = ctx.getSource().getPlayerOrException();
        ItemStack mainHandItem = player.getMainHandItem();
        String serialize = new ItemInput(mainHandItem.getItemHolder(), mainHandItem.getComponentsPatch()).serialize(player.getServer().registryAccess());
        ctx.getSource().sendSystemMessage(Component.literal("Exported Click to Copy").withStyle(style -> style
                .withClickEvent(new ClickEvent.CopyToClipboard(serialize))));
        return Command.SINGLE_SUCCESS;
    }

}
