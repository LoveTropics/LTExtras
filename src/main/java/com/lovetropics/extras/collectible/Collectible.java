package com.lovetropics.extras.collectible;

import com.lovetropics.extras.ExtraDataComponents;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.UUID;

public class Collectible implements DataComponentHolder {
    public static final Codec<Collectible> CODEC = RecordCodecBuilder.create(i -> i.group(
            BuiltInRegistries.ITEM.holderByNameCodec().fieldOf("item").forGetter(c -> c.item),
            DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(c -> c.components)
    ).apply(i, Collectible::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, Collectible> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.holderRegistry(Registries.ITEM), c -> c.item,
            DataComponentPatch.STREAM_CODEC, c -> c.components,
            Collectible::new
    );

    private final Holder<Item> item;
    private final DataComponentPatch components;
    private final DataComponentMap combinedComponents;

    private Collectible(final Holder<Item> item, final DataComponentPatch components) {
        this.item = item;
        this.components = components;
        combinedComponents = PatchedDataComponentMap.fromPatch(item.value().components(), components);
    }

    public Collectible(final ItemStack stack) {
        this(stack.getItemHolder(), componentsWithoutMarker(stack.getComponentsPatch()));
    }

    @Nullable
    public static Collectible byItem(final ItemStack stack) {
        if (isCollectible(stack)) {
            return new Collectible(stack);
        }
        return null;
    }

    public static boolean isCollectible(final ItemStack stack) {
        return stack.has(ExtraDataComponents.COLLECTIBLE_OWNER);
    }

    public static boolean isIllegalCollectible(final ItemStack stack, final Player player) {
        final UUID owner = stack.get(ExtraDataComponents.COLLECTIBLE_OWNER);
        return owner != null && !player.getUUID().equals(owner);
    }

    public ItemStack createItemStack(final UUID player) {
        final ItemStack stack = new ItemStack(item);
        stack.applyComponents(components);
        addMarkerTo(player, stack);
        return stack;
    }

    public static void addMarkerTo(final UUID player, final ItemStack stack) {
        stack.set(ExtraDataComponents.COLLECTIBLE_OWNER, player);
    }

    public Holder<Item> item() {
        return item;
    }

    public DataComponentPatch components() {
        return components;
    }

    public boolean matches(final ItemStack stack) {
        if (!stack.is(item) || !Collectible.isCollectible(stack)) {
            return false;
        }
        return components.equals(componentsWithoutMarker(stack.getComponentsPatch()));
    }

    private static DataComponentPatch componentsWithoutMarker(DataComponentPatch components) {
        return components.forget(type -> type == ExtraDataComponents.COLLECTIBLE_OWNER.value());
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof final Collectible collectible) {
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
