package com.lovetropics.extras.template;

import com.google.gson.JsonElement;
import com.lovetropics.extras.data.Named;
import com.lovetropics.extras.data.SimpleDataPackLister;
import com.lovetropics.extras.registry.ExtraRegistries;
import com.lovetropics.extras.template.item.ItemTemplate;
import com.lovetropics.lib.codec.CodecRegistry;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@EventBusSubscriber
public class TemplateConfigs {

    public static final CodecRegistry<Identifier, Named<ItemTemplate>> ITEM_TEMPLATES = CodecRegistry.idKeys();

    public static final SimpleDataPackLister<ItemTemplate> ITEM_TEMPLATE_LISTER = new SimpleDataPackLister<>("ltextras/item_template", ExtraRegistries.ITEM_TEMPLATE, ItemTemplate.CODEC);

    private static final RegistrationInfo DEFAULT_REGISTRATION_INFO = new RegistrationInfo(Optional.empty(), Lifecycle.experimental());

    public static CompletableFuture<WritableRegistry<LootTable>> decorateAndLoad(CompletableFuture<WritableRegistry<LootTable>> lootTablesFuture, ResourceManager resourceManager, DynamicOps<JsonElement> ops, Executor taskExecutor) {
        return lootTablesFuture.thenCombine(
                ITEM_TEMPLATE_LISTER.load(ops, resourceManager, taskExecutor),
                (lootTables, itemTemplates) -> {
                    ITEM_TEMPLATES.setAll(itemTemplates.stream().collect(Collectors.toMap(
                            t -> t.key().identifier(),
                            t -> t
                    )));
                    for (Named<ItemTemplate> entry : itemTemplates) {
                        ResourceKey<LootTable> tableId = ResourceKey.create(Registries.LOOT_TABLE, entry.id().withPrefix("template/"));
                        lootTables.register(tableId, ItemTemplate.asLootTable(entry), DEFAULT_REGISTRATION_INFO);
                    }
                    return lootTables;
                }
        );
    }
}
