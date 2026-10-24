package com.lovetropics.extras.template;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.data.Named;
import com.lovetropics.extras.data.SimpleDataPackLister;
import com.lovetropics.extras.registry.CustomRegistryReloadListener;
import com.lovetropics.extras.registry.ExtraRegistries;
import com.lovetropics.extras.template.item.ItemTemplate;
import com.lovetropics.lib.codec.CodecRegistry;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;

@EventBusSubscriber
public class TemplateConfigs {

    public static final CodecRegistry<Identifier, Named<ItemTemplate>> ITEM_TEMPLATES = CodecRegistry.idKeys();

    private static final SimpleDataPackLister<ItemTemplate> ITEM_TEMPLATE_LISTENER = new SimpleDataPackLister<>("ltextras/item_template", ExtraRegistries.ITEM_TEMPLATE, ItemTemplate.CODEC);

    @SubscribeEvent
    public static void addReloadListener(AddServerReloadListenersEvent event) {
        event.addListener(LTExtras.id("item_template"), CustomRegistryReloadListener.of(ITEM_TEMPLATE_LISTENER, ITEM_TEMPLATES));
    }
}
