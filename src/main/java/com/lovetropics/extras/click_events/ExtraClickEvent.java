package com.lovetropics.extras.click_events;

import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerPlayer;

public interface ExtraClickEvent {

    void handleAction(ServerPlayer serverPlayer);

    MapCodec<? extends ExtraClickEvent> getCodec();
}
