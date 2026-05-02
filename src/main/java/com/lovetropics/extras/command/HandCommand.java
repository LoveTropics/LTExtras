package com.lovetropics.extras.command;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.Optional;

import static net.minecraft.commands.Commands.literal;

public class HandCommand {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        // @formatter:off
        dispatcher.register(
                literal("hand").requires(Commands.hasPermission(Commands.LEVEL_OWNERS))
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
        RegistryOps<JsonElement> registryops = player.level().getServer().registryAccess().createSerializationContext(JsonOps.INSTANCE);
        String json = GSON.toJson(ItemStack.CODEC.encodeStart(registryops, heldItem).getOrThrow());
        ctx.getSource().sendSystemMessage(Component.literal("Exported Click to Copy").withStyle(style -> style
                .withClickEvent(new ClickEvent.CopyToClipboard(json))));
        return Command.SINGLE_SUCCESS;
    }

    private static int displayItem(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Player player = ctx.getSource().getPlayerOrException();
        ItemStack mainHandItem = player.getMainHandItem();
        String serialize = serializeItem(ctx, mainHandItem);
        ctx.getSource().sendSystemMessage(Component.literal("Exported Click to Copy").withStyle(style -> style
                .withClickEvent(new ClickEvent.CopyToClipboard(serialize))));
        return Command.SINGLE_SUCCESS;
    }

    private static String serializeItem(CommandContext<CommandSourceStack> ctx, ItemStack mainHandItem) {
        StringBuilder output = new StringBuilder();
        output.append(mainHandItem.typeHolder().getRegisteredName());

        DataComponentPatch components = mainHandItem.getComponentsPatch();
        if (components.isEmpty()) {
            return output.toString();
        }

        RegistryOps<Tag> ops = ctx.getSource().registryAccess().createSerializationContext(NbtOps.INSTANCE);
        output.append('[');
        for (Map.Entry<DataComponentType<?>, Optional<?>> entry : components.entrySet()) {
            DataComponentType<?> component = entry.getKey();
            if (component.codec() == null) {
                continue;
            }
            Identifier componentId = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(component);
            if (entry.getValue().isPresent()) {
                output.append(componentId).append('=');
                output.append(serializeComponentUnchecked(ops, component, entry.getValue().get()));
            } else {
                output.append('!').append(componentId);
            }
        }
        output.append(']');

        return output.toString();
    }

    @SuppressWarnings("unchecked")
    private static <T> Tag serializeComponentUnchecked(DynamicOps<Tag> ops, DataComponentType<T> type, Object value) {
        return type.codecOrThrow().encodeStart(ops, (T) value).getOrThrow();
    }
}
