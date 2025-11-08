package com.lovetropics.extras.click_events;

import com.mojang.serialization.MapCodec;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Consumer;

public interface ExtraClickEvent {

    void handleAction(ServerPlayer serverPlayer, Tag tag, Consumer<Component> errorHandler);

    MapCodec<? extends ExtraClickEvent> getCodec();
}
