package com.lovetropics.extras.client.entity.state;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.data.attachment.ExtraAttachments;
import net.minecraft.client.renderer.entity.state.ShulkerRenderState;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.monster.Shulker;

public class HoniedShulkerRenderState {

    public static final ContextKey<Boolean> HONIED = new ContextKey<>(LTExtras.id("shulker/honied"));

    public static void updateHoniedRenderState(Shulker entity, ShulkerRenderState renderState) {
        renderState.setRenderData(HONIED, entity.getData(ExtraAttachments.HONIED));
    }
}
