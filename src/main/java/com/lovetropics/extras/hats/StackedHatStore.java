package com.lovetropics.extras.hats;

import com.lovetropics.extras.data.attachment.ExtraAttachments;
import com.lovetropics.lib.item.StackedHat;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record StackedHatStore(List<StackedHat> stackedHats) {

    public static final MapCodec<StackedHatStore> MAP_CODEC = Codec.list(StackedHat.CODEC)
            .xmap(StackedHatStore::of, StackedHatStore::stackedHats)
            .fieldOf("hats");

    public static final StreamCodec<RegistryFriendlyByteBuf, StackedHatStore> STREAM_CODEC = StreamCodec.composite(
            StackedHat.STREAM_CODEC.apply(ByteBufCodecs.list()), StackedHatStore::stackedHats,
            StackedHatStore::of);

    public static final StackedHatStore EMPTY = StackedHatStore.of(Collections.emptyList());

    public static StackedHatStore of(List<StackedHat> stackedHats) {
        return new StackedHatStore(new ArrayList<>(stackedHats));
    }

    public static void addHat(Entity entity, StackedHat hat) {
        StackedHatStore store = getOrDefault(entity);
        store.stackedHats.add(hat);
        entity.syncData(ExtraAttachments.HATS);
    }

    public static void removeHat(Entity entity, StackedHat hat) {
        StackedHatStore store = getOrDefault(entity);
        store.stackedHats.remove(hat);
        entity.syncData(ExtraAttachments.HATS);
    }

    public static void clearHats(Entity entity) {
        StackedHatStore store = getOrDefault(entity);
        store.stackedHats.clear();
        entity.syncData(ExtraAttachments.HATS);
    }

    public static void setHats(Entity entity, List<StackedHat> hats) {
        StackedHatStore newStore = StackedHatStore.of(hats);
        entity.setData(ExtraAttachments.HATS, newStore);
    }

    public static List<StackedHat> getHats(Entity entity) {
        return getOrDefault(entity).stackedHats;
    }

    public static StackedHatStore getOrDefault(Entity entity) {
        return entity.getData(ExtraAttachments.HATS);
    }
}
