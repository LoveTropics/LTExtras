package com.lovetropics.extras.data.attachment;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.collectible.CollectibleStore;
import com.lovetropics.extras.data.spawnitems.SpawnItemsStore;
import com.lovetropics.extras.schedule.PlayerTimeZone;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ExtraAttachments {
    public static final DeferredRegister<AttachmentType<?>> REGISTER = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, LTExtras.MODID);

    public static final Supplier<AttachmentType<PlayerTimeZone>> TIME_ZONE = REGISTER.register(
            "time_zone", () -> AttachmentType.builder(PlayerTimeZone::new).build()
    );

    public static final Supplier<AttachmentType<CollectibleStore>> COLLECTABLE_STORE = REGISTER.register(
            "collectable_store", () -> AttachmentType.builder(CollectibleStore::new).build()
    );

    public static final Supplier<AttachmentType<SpawnItemsStore>> SPAWN_ITEMS_STORE = REGISTER.register(
            "spawn_items_store", () -> AttachmentType.builder(SpawnItemsStore::new).build()
    );
}
