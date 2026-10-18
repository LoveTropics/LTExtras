package com.lovetropics.extras.loot;

import com.lovetropics.extras.ExtraDataComponents;
import com.lovetropics.extras.collectible.Collectible;
import com.lovetropics.extras.collectible.CollectibleLock;
import com.lovetropics.extras.collectible.CollectibleMarker;
import com.lovetropics.extras.registry.ExtraRegistries;
import com.lovetropics.lib.codec.MoreCodecs;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.UUID;

/**
 * LTExtras
 * FIRE EMOJI, FIRE EMOJI, FIRE EMOJI
 */
@SuppressWarnings("NullableProblems") // This feels like it's calling me out
public class LootItem {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final Codec<LootItem> DIRECT_CODEC = RecordCodecBuilder.create( i -> i.group(
            MoreCodecs.SINGLE_STACK_TEMPLATE.fieldOf("item").forGetter(c -> c.item)
    ).apply(i, LootItem::new));

    public static final Codec<Holder<LootItem>> CODEC = RegistryFileCodec.create(ExtraRegistries.LOOT_ITEM, DIRECT_CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, LootItem> DIRECT_STREAM_CODEC = StreamCodec.composite(
            ItemStackTemplate.STREAM_CODEC, c -> c.item,
            LootItem::new
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<LootItem>> STREAM_CODEC = ByteBufCodecs.holder(ExtraRegistries.LOOT_ITEM, DIRECT_STREAM_CODEC);

    private final ItemStackTemplate item;

    private LootItem(ItemStackTemplate item) {
        this.item = item;
        LOGGER.error("Registered LootItem with name {}", item.item().value().getDescriptionId());
    }

    public LootItem(ItemStack stack) {
        this(ItemStackTemplate.fromNonEmptyStack(stack));
    }

    public static @Nullable Holder<LootItem> byItem(ItemStack stack) {
        LootItemMarker marker = stack.get(ExtraDataComponents.LOOT_ITEM);
        if (marker == null) {
            return null;
        }
        return marker.lootItem().orElseGet(() -> Holder.direct(new LootItem(ItemStackTemplate.fromNonEmptyStack(stack))));
    }

    public ItemStackTemplate item() {
        return item;
    }

    public static ItemStack createItemStack(Holder<LootItem> lootItem) {
        ItemStack stack = createUnmarkedItemStack(lootItem);
        addMarkerTo(lootItem, stack);
        return stack;
    }

    public static ItemStack createUnmarkedItemStack(Holder<LootItem> lootItem) {
        return lootItem.value().item.create();
    }

    public static void addMarkerTo(Holder<LootItem> lootItem, ItemStack stack) {
        stack.set(ExtraDataComponents.LOOT_ITEM, new LootItemMarker(
                lootItem.kind() == Holder.Kind.REFERENCE ? Optional.of(lootItem) : Optional.empty()
        ));
    }

    public static boolean matches(Holder<LootItem> lootItem, ItemStack stack) {
        LootItemMarker marker = stack.get(ExtraDataComponents.LOOT_ITEM);
        if (marker == null) {
            return false;
        }
        if (marker.lootItem().isPresent()) {
            // If the item specifies its source collectible (loot item), we don't care if the item looks exactly the same
            return marker.lootItem().get().equals(lootItem);
        } else {
            if (!stack.is(lootItem.value().item().item())) {
                return false;
            }
            return lootItem.value().item().components().equals(componentsWithoutMarker(stack.getComponentsPatch()));
        }
    }

    private static DataComponentPatch componentsWithoutMarker(DataComponentPatch components) {
        return components.forget(type -> type == ExtraDataComponents.LOOT_ITEM.value());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof LootItem lootItem) {
            return item.equals(lootItem.item);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return item.hashCode();
    }
}
