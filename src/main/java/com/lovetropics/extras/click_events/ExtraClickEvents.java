package com.lovetropics.extras.click_events;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.lib.codec.CodecRegistry;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandResultCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.FunctionInstantiationException;
import net.minecraft.commands.execution.ExecutionContext;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.commands.functions.InstantiatedFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.enchantment.effects.RunFunction;
import org.slf4j.Logger;

import java.util.Optional;

public class ExtraClickEvents {

    private static final Logger LOGGER = LogUtils.getLogger();

    private static final CodecRegistry<ResourceLocation, MapCodec<? extends ExtraClickEvent>> REGISTRY = Util.make(CodecRegistry.resourceLocationKeys(), registry -> {
        registry.register(LTExtras.location("run_function"), RunFunctionClickEvent.CODEC);
    });

    public static void handleCustomClickAction(ServerPlayer serverPlayer, ResourceLocation location, Optional<Tag> tag) {
        MapCodec<? extends ExtraClickEvent> mapCodec = REGISTRY.get(location);
        if (mapCodec == null) {
            sendErrorMessage(serverPlayer, "Unknown click action: " + location);
            LOGGER.error("Received unknown click action location: {}", location);
            return;
        }

        Tag input = tag.orElse(new CompoundTag());
        try {
            mapCodec.codec().parse(NbtOps.INSTANCE, input).getOrThrow().handleAction(serverPlayer);
        } catch (Exception e) {
            sendErrorMessage(serverPlayer, "Failed to decode click action payload for action: " + location);
            LOGGER.error("Failed to decode click action payload for location: {}", location, e);
        }
    }

    private static void sendErrorMessage(ServerPlayer serverPlayer, String message) {
        serverPlayer.sendSystemMessage(Component.literal(message).withStyle(ChatFormatting.RED));
    }
}
