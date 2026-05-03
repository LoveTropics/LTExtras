package com.lovetropics.extras.compact;

import com.lovetropics.extras.LTExtras;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddPackFindersEvent;

@EventBusSubscriber
public class ContexCompact {

    private static final String CONTEX_MOD_ID = "contex";

    @SubscribeEvent
    public static void onAddPackFinders(AddPackFindersEvent event) {
        if (event.getPackType() != PackType.CLIENT_RESOURCES) {
            return;
        }

        if (!ModList.get().isLoaded(CONTEX_MOD_ID)) {
            return;
        }

        event.addPackFinders(
                LTExtras.id("contex"),
                PackType.CLIENT_RESOURCES,
                Component.literal("LTExtras Connected Textures"),
                PackSource.BUILT_IN,
                true,
                Pack.Position.TOP);
    }
}
