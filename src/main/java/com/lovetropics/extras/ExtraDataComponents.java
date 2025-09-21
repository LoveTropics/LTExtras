package com.lovetropics.extras;

import com.lovetropics.extras.collectible.CollectibleMarker;
import com.lovetropics.extras.data.poi.MapConfig;
import com.lovetropics.extras.item.CollectibleCompassItem;
import com.lovetropics.extras.item.ImageData;
import com.lovetropics.extras.item.sensor.PlayerSensor;
import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.UUID;

@EventBusSubscriber(modid = LTExtras.MODID)
public class ExtraDataComponents {
    public static final DeferredRegister.DataComponents REGISTER = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, LTExtras.MODID);

    // Components for specific items
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CollectibleMarker>> COLLECTIBLE = REGISTER.registerComponentType(
            "collectible",
            builder -> builder.persistent(CollectibleMarker.CODEC).networkSynchronized(CollectibleMarker.STREAM_CODEC).cacheEncoding()
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CollectibleCompassItem.Target>> COLLECTIBLE_TARGET = REGISTER.registerComponentType(
            "collectible_target",
            builder -> builder.persistent(CollectibleCompassItem.Target.CODEC).networkSynchronized(CollectibleCompassItem.Target.STREAM_CODEC).cacheEncoding()
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> COIN_COUNT = REGISTER.registerComponentType(
            "coin_count",
            builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT)
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<UUID>> TARGETED_ENTITY = REGISTER.registerComponentType(
            "targeted_entity",
            builder -> builder.persistent(UUIDUtil.CODEC)
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BlockPos>> JUMP_PAD = REGISTER.registerComponentType(
            "jump_pad",
            builder -> builder.persistent(BlockPos.CODEC).networkSynchronized(BlockPos.STREAM_CODEC).cacheEncoding()
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Holder<MapConfig>>> MAP = REGISTER.registerComponentType(
            "map",
            builder -> builder.persistent(MapConfig.CODEC).networkSynchronized(MapConfig.STREAM_CODEC).cacheEncoding()
    );

    // General extensions
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> UNDROPPABLE = REGISTER.registerComponentType(
            "undroppable",
            builder -> builder.persistent(Codec.unit(Unit.INSTANCE)).networkSynchronized(StreamCodec.unit(Unit.INSTANCE))
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> COOLDOWN_OVERRIDE = REGISTER.registerComponentType(
            "cooldown_override",
            builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT).cacheEncoding()
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ImageData>> IMAGE = REGISTER.registerComponentType(
            "image",
            builder -> builder.persistent(ImageData.CODEC).cacheEncoding()
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<PlayerSensor>> PLAYER_SENSOR = REGISTER.registerComponentType(
            "player_sensor",
            builder -> builder.persistent(PlayerSensor.CODEC).networkSynchronized(PlayerSensor.STREAM_CODEC).cacheEncoding()
    );

    @SubscribeEvent
    public static void addToTooltip(ItemTooltipEvent event) {
        ItemStack itemStack = event.getItemStack();
        List<Component> tooltip = event.getToolTip();

        Holder<MapConfig> map = itemStack.get(ExtraDataComponents.MAP);
        if (map != null) {
            tooltip.add(ComponentUtils.mergeStyles(map.value().description().copy(), Style.EMPTY.withColor(ChatFormatting.GRAY)));
        }
    }
}
