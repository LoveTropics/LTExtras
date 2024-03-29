package com.lovetropics.extras.collectible;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class Collectible {
    private static final Codec<CompoundTag> TAG_CODEC = CompoundTag.CODEC.xmap(CompoundTag::copy, CompoundTag::copy);
    public static final Codec<Collectible> CODEC = RecordCodecBuilder.create(i -> i.group(
            BuiltInRegistries.ITEM.holderByNameCodec().fieldOf("item").forGetter(c -> c.item),
            TAG_CODEC.optionalFieldOf("tag").forGetter(c -> c.tag)
    ).apply(i, Collectible::new));

    private static final String KEY_ITEM_STACK_MARKER = "collectible_marker";

    private final Holder<Item> item;
    private final Optional<CompoundTag> tag;

    private Collectible(final Holder<Item> item, final Optional<CompoundTag> tag) {
        this.item = item;
        this.tag = tag.filter(t -> !t.isEmpty());
    }

    public Collectible(final ItemStack stack) {
        this(stack.getItemHolder(), Optional.ofNullable(stack.getTag()).map(CompoundTag::copy).map(t -> {
            t.remove(KEY_ITEM_STACK_MARKER);
            return t;
        }));
    }

    public Collectible(final FriendlyByteBuf input) {
        this(input.readById(BuiltInRegistries.ITEM.asHolderIdMap()), input.readOptional(FriendlyByteBuf::readNbt));
    }

    public void write(final FriendlyByteBuf output) {
        output.writeId(BuiltInRegistries.ITEM.asHolderIdMap(), item);
        output.writeOptional(tag, FriendlyByteBuf::writeNbt);
    }

    @Nullable
    public static Collectible byItem(final ItemStack stack) {
        if (isCollectible(stack)) {
            return new Collectible(stack);
        }
        return null;
    }

    public static boolean isCollectible(final ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        final CompoundTag tag = stack.getTag();
        return tag != null && tag.contains(KEY_ITEM_STACK_MARKER);
    }

    public static boolean isIllegalCollectible(final ItemStack stack, final Player player) {
        if (stack.isEmpty()) {
            return false;
        }
        final CompoundTag tag = stack.getTag();
        if (tag != null && tag.hasUUID(KEY_ITEM_STACK_MARKER)) {
            return !player.getUUID().equals(tag.getUUID(KEY_ITEM_STACK_MARKER));
        }
        return false;
    }

    public ItemStack createItemStack(final UUID player) {
        final ItemStack stack = new ItemStack(item);
        tag.map(CompoundTag::copy).ifPresent(stack::setTag);
        addMarkerTo(player, stack);
        return stack;
    }

    public static void addMarkerTo(final UUID player, final ItemStack stack) {
        stack.getOrCreateTag().putUUID(KEY_ITEM_STACK_MARKER, player);
    }

    public Holder<Item> item() {
        return item;
    }

    public Optional<CompoundTag> tag() {
        return tag;
    }

    public boolean matches(final ItemStack stack) {
        if (!stack.is(item) || !Collectible.isCollectible(stack)) {
            return false;
        }
        final CompoundTag tag = getTagWithoutMarker(stack);
        return tag.equals(this.tag.orElseGet(CompoundTag::new));
    }

    private static CompoundTag getTagWithoutMarker(final ItemStack stack) {
        final CompoundTag tag = stack.getOrCreateTag().copy();
        tag.remove(KEY_ITEM_STACK_MARKER);
        return tag;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof final Collectible collectible) {
            return item.equals(collectible.item) && Objects.equals(tag, collectible.tag);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return item.hashCode() * 31 + Objects.hashCode(tag);
    }
}
