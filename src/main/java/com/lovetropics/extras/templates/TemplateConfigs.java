package com.lovetropics.extras.templates;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.data.Named;
import com.lovetropics.extras.data.SimpleDataPackLister;
import com.lovetropics.extras.registry.CustomRegistryReloadListener;
import com.lovetropics.extras.registry.ExtraRegistries;
import com.lovetropics.lib.codec.CodecRegistry;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;

@EventBusSubscriber
public class TemplateConfigs {
    public static final CodecRegistry<Identifier, Named<ItemStackTemplate>> ITEM_TEMPLATE = CodecRegistry.idKeys();

    private static final SimpleDataPackLister<ItemStackTemplate> ITEM_TEMPLATE_LISTENER = new SimpleDataPackLister<>("ltextras/item_templates", ExtraRegistries.ITEM_TEMPLATES, ItemStackTemplate.CODEC);

    @SubscribeEvent
    public static void addReloadListener(AddServerReloadListenersEvent event) {
        event.addListener(LTExtras.id("mounts"), CustomRegistryReloadListener.of(ITEM_TEMPLATE_LISTENER, ITEM_TEMPLATE));
    }
}
