package com.lovetropics.extras.data.attachment;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.collectible.CollectibleStore;
import com.lovetropics.extras.consume_actions.DisguiseConsumeAction;
import com.lovetropics.extras.data.TropiCoinsStore;
import com.lovetropics.extras.data.spawnitems.SpawnItemsStore;
import com.lovetropics.extras.extension.CustomTradeExtension;
import com.lovetropics.extras.model_modifer.ModelModifierStore;
import com.lovetropics.extras.mounts.MountStore;
import com.lovetropics.extras.schedule.PlayerTimeZone;
import com.lovetropics.extras.shield.Shields;
import com.lovetropics.extras.zipline.ZiplineIndex;
import com.lovetropics.extras.zipline.ZiplineRider;
import com.mojang.serialization.Codec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Collections;

public class ExtraAttachments {
    public static final DeferredRegister<AttachmentType<?>> REGISTER = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, LTExtras.MODID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<PlayerTimeZone>> TIME_ZONE = REGISTER.register(
            "time_zone", () -> AttachmentType.builder(PlayerTimeZone::new).build()
    );

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<TropiCoinsStore>> TROPICOINS_STORE = REGISTER.register(
            "tropicoins_store", () -> AttachmentType.builder(TropiCoinsStore::new)
                    .serialize(TropiCoinsStore.CODEC)
                    .copyOnDeath()
                    .sync(TropiCoinsStore.STREAM_CODEC)
                    .build()
    );

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<CollectibleStore>> COLLECTIBLE_STORE = REGISTER.register(
            "collectible_store", () -> AttachmentType.builder(CollectibleStore::new)
                    .serialize(CollectibleStore.MAP_CODEC)
                    .build()
    );

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<SpawnItemsStore>> SPAWN_ITEMS_STORE = REGISTER.register(
            "spawn_items_store", () -> AttachmentType.builder(SpawnItemsStore::new)
                    .serialize(SpawnItemsStore.MAP_CODEC)
                    .build()
    );

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<ZiplineRider>> ZIPLINE_RIDER = REGISTER.register(
            "zipline_rider", () -> AttachmentType.builder(ZiplineRider::new).build()
    );

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<ZiplineIndex>> ZIPLINE_INDEX = REGISTER.register(
            "zipline_index", () -> AttachmentType.builder(ZiplineIndex::new).build()
    );

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<ModelModifierStore>> MODEL_MODIFIERS = REGISTER.register(
            "model_modifiers", () -> AttachmentType.builder(() -> ModelModifierStore.of(Collections.emptyList()))
                    .serialize(ModelModifierStore.MAP_CODEC)
                    .sync(ModelModifierStore.STREAM_CODEC)
                    .build()
    );

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> HONIED = REGISTER.register(
            "honied", () -> AttachmentType.builder(() -> false)
                    .serialize(Codec.BOOL.fieldOf("honied"))
                    .sync(ByteBufCodecs.BOOL)
                    .build()
    );

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<MountStore>> MOUNT = REGISTER.register(
            "mount_store", () -> AttachmentType.builder(MountStore::new)
                    .serialize(MountStore.MAP_CODEC)
                    .build()
    );

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<DisguiseConsumeAction.DisguiseTimerStore>> DISGUISE_TIMER_STORE = REGISTER.register(
            "disguise_timer_store", () -> AttachmentType.builder(() -> DisguiseConsumeAction.DisguiseTimerStore.EMPTY)
                    .serialize(DisguiseConsumeAction.DisguiseTimerStore.CODEC)
                    .build()
    );

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<CustomTradeExtension>> TRADE_OVERRIDE = REGISTER.register(
            "trade_override", () -> AttachmentType.builder(() -> CustomTradeExtension.EMPTY)
                    .serialize(CustomTradeExtension.TRADE_CODEC)
                    .build()
    );

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Double>> SHIELD_RADIUS = REGISTER.register(
            "shield_radius", () -> AttachmentType.builder(() -> Shields.DEFAULT_RADIUS)
                    .serialize(Codec.DOUBLE.fieldOf("radius"))
                    .copyOnDeath()
                    .build()
    );
}
