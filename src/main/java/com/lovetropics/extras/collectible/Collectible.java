package com.lovetropics.extras.collectible;

import com.lovetropics.extras.ExtraDataComponents;
import com.lovetropics.extras.registry.ExtraRegistries;
import com.lovetropics.lib.codec.MoreCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class Collectible{
    public static final Codec<Collectible> DIRECT_CODEC = RecordCodecBuilder.create(i -> i.group(
            MoreCodecs.SINGLE_STACK_TEMPLATE.fieldOf("item").forGetter(c -> c.item),
            Codec.BOOL.fieldOf("auto_equip").orElse(false).forGetter(c -> c.autoEquip)
    ).apply(i, Collectible::new));
    public static final Codec<Holder<Collectible>> CODEC = RegistryFileCodec.create(ExtraRegistries.COLLECTIBLE, DIRECT_CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, Collectible> DIRECT_STREAM_CODEC = StreamCodec.composite(
            ItemStackTemplate.STREAM_CODEC, c -> c.item,
            ByteBufCodecs.BOOL, c -> c.autoEquip,
            Collectible::new
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<Collectible>> STREAM_CODEC = ByteBufCodecs.holder(ExtraRegistries.COLLECTIBLE, DIRECT_STREAM_CODEC);

    private final ItemStackTemplate item;
    private final boolean autoEquip;

    private Collectible(ItemStackTemplate item, boolean autoEquip) {
        this.item = item;
        this.autoEquip = autoEquip;
    }

    public Collectible(ItemStackTemplate stack) {
        this(stack, false);
    }

    public Collectible(ItemStack stack) {
        this(ItemStackTemplate.fromNonEmptyStack(stack));
    }

    public static @Nullable Holder<Collectible> byItem(ItemStack stack) {
        CollectibleMarker marker = stack.get(ExtraDataComponents.COLLECTIBLE);
        if (marker == null) {
            return null;
        }
        return marker.collectible().orElseGet(() -> Holder.direct(new Collectible(ItemStackTemplate.fromNonEmptyStack(stack))));
    }

    public static boolean isCollectible(ItemStack stack) {
        return stack.has(ExtraDataComponents.COLLECTIBLE);
    }

    public static boolean isIllegalCollectible(ItemStack stack, Player player) {
        CollectibleMarker marker = stack.get(ExtraDataComponents.COLLECTIBLE);
        if (marker == null) {
            return false;
        }
        return marker.ownerId().isPresent() && !player.getUUID().equals(marker.ownerId().get());
    }

    public static ItemStack createItemStack(Holder<Collectible> collectible, UUID player) {
        ItemStack stack = createUnmarkedItemStack(collectible);
        addMarkerTo(player, collectible, stack);
        return stack;
    }

    public static ItemStack createUnmarkedItemStack(Holder<Collectible> collectible) {
        return collectible.value().item.create();
    }

    public static void addMarkerTo(UUID player, Holder<Collectible> collectible, ItemStack stack) {
        stack.set(ExtraDataComponents.COLLECTIBLE, new CollectibleMarker(
                collectible.kind() == Holder.Kind.REFERENCE ? Optional.of(collectible) : Optional.empty(),
                Optional.of(player)
        ));
    }

    public boolean autoEquip() {
        return autoEquip;
    }

    public ItemStackTemplate item() {
        return item;
    }

    public static boolean matches(Holder<Collectible> collectible, ItemStack stack) {
        CollectibleMarker marker = stack.get(ExtraDataComponents.COLLECTIBLE);
        if (marker == null) {
            return false;
        }
        if (marker.collectible().isPresent()) {
            // If the item specifies its source collectible, we don't care if the item looks exactly the same
            return marker.collectible().get().equals(collectible);
        } else {
            if (!stack.is(collectible.value().item().item())) {
                return false;
            }
            return collectible.value().item().components().equals(componentsWithoutMarker(stack.getComponentsPatch()));
        }
    }

    private static DataComponentPatch componentsWithoutMarker(DataComponentPatch components) {
        return components.forget(type -> type == ExtraDataComponents.COLLECTIBLE.value());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Collectible collectible) {
            return item.equals(collectible.item) && autoEquip == collectible.autoEquip;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return item.hashCode() * 31 + Boolean.hashCode(autoEquip);
    }

}
