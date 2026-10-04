package com.lovetropics.extras.consume_actions;

import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public interface ConsumeAction {

    void onConsume(ServerPlayer serverPlayer, ItemStack stack);

    MapCodec<? extends ConsumeAction> getCodec();
}
