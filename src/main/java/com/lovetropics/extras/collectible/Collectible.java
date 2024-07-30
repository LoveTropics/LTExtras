package com.lovetropics.extras.collectible;

import com.lovetropics.extras.ExtraDataComponents;
import com.lovetropics.extras.registry.ExtraRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class Collectible implements DataComponentHolder {
    public static final Codec<Collectible> DIRECT_CODEC = RecordCodecBuilder.create(i -> i.group(
            BuiltInRegistries.ITEM.holderByNameCodec().fieldOf("item").forGetter(c -> c.item),
            DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(c -> c.components)
    ).apply(i, Collectible::new));
    public static final Codec<Holder<Collectible>> CODEC = RegistryFileCodec.create(ExtraRegistries.COLLECTIBLE, DIRECT_CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, Collectible> DIRECT_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.holderRegistry(Registries.ITEM), c -> c.item,
            DataComponentPatch.STREAM_CODEC, c -> c.components,
            Collectible::new
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<Collectible>> STREAM_CODEC = ByteBufCodecs.holder(ExtraRegistries.COLLECTIBLE, DIRECT_STREAM_CODEC);

    private final Holder<Item> item;
    private final DataComponentPatch components;
    private final DataComponentMap combinedComponents;

    private Collectible(Holder<Item> item, DataComponentPatch components) {
        this.item = item;
        this.components = components;
        combinedComponents = PatchedDataComponentMap.fromPatch(item.value().components(), components);
    }

    public Collectible(ItemStack stack) {
        this(stack.getItemHolder(), componentsWithoutMarker(stack.getComponentsPatch()));
    }

    @Nullable
    public static Holder<Collectible> byItem(ItemStack stack) {
        CollectibleMarker marker = stack.get(ExtraDataComponents.COLLECTIBLE);
        if (marker == null) {
            return null;
        }
        return marker.collectible().orElseGet(() -> Holder.direct(new Collectible(stack)));
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
        ItemStack stack = new ItemStack(collectible.value().item());
        stack.applyComponents(collectible.value().components());
        addMarkerTo(player, collectible, stack);
        return stack;
    }

    public static void addMarkerTo(UUID player, Holder<Collectible> collectible, ItemStack stack) {
        stack.set(ExtraDataComponents.COLLECTIBLE, new CollectibleMarker(
                collectible.kind() == Holder.Kind.REFERENCE ? Optional.of(collectible) : Optional.empty(),
                Optional.of(player)
        ));
    }

    public Holder<Item> item() {
        return item;
    }

    public DataComponentPatch components() {
        return components;
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
            if (!stack.is(collectible.value().item())) {
                return false;
            }
            return collectible.value().components().equals(componentsWithoutMarker(stack.getComponentsPatch()));
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
            return item.equals(collectible.item) && components.equals(collectible.components);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return item.hashCode() * 31 + components.hashCode();
    }

    @Override
    public DataComponentMap getComponents() {
        return combinedComponents;
    }
}
