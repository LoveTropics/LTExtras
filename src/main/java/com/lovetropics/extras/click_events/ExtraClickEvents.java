package com.lovetropics.extras.click_events;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.lib.codec.CodecRegistry;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Util;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.CustomClickActionEvent;
import org.slf4j.Logger;

import java.util.Optional;

@EventBusSubscriber
public class ExtraClickEvents {

    private static final Logger LOGGER = LogUtils.getLogger();

    private static final CodecRegistry<Identifier, MapCodec<? extends ExtraClickEvent>> REGISTRY = Util.make(CodecRegistry.idKeys(), registry -> {
        registry.register(LTExtras.id("run_function"), RunFunctionClickEvent.CODEC);
        registry.register(LTExtras.id("mount_entity"), MountClickEvent.CODEC);
    });

    private static void sendErrorMessage(ServerPlayer serverPlayer, Component message) {
        serverPlayer.sendSystemMessage(message.copy().withStyle(ChatFormatting.RED));
    }

    @SubscribeEvent
    public static void onCustomClick(CustomClickActionEvent event) {
        ServerPlayer player = event.getPlayer();
        if (player == null) {
            return; 
        }
        Identifier id = event.getIdentifier();
        MapCodec<? extends ExtraClickEvent> clickCodec = REGISTRY.get(id);
        if (clickCodec == null) {
            return;
        }
        Tag tag = event.getPayload() == null ? new CompoundTag() : event.getPayload();

        try {
            clickCodec.codec().parse(NbtOps.INSTANCE, tag).getOrThrow().handleAction(event.getPlayer(), tag, message -> sendErrorMessage(player, message));
        } catch (Exception e) {
            sendErrorMessage(player, Component.literal("Failed to decode click action payload for action: " + id));
            LOGGER.error("Failed to decode click action payload for location: {}", id, e);
        }
    }
}
