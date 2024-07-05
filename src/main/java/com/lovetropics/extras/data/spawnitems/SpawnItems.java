package com.lovetropics.extras.data.spawnitems;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public record SpawnItems(List<Stack> items, boolean canBeRestored, Optional<String> excludeTag) {
    public static final Codec<SpawnItems> CODEC = RecordCodecBuilder.create(in -> in.group(
            Stack.CODEC.listOf().fieldOf("items").forGetter(SpawnItems::items),
            Codec.BOOL.optionalFieldOf("can_be_restored", true).forGetter(SpawnItems::canBeRestored),
            Codec.STRING.optionalFieldOf("exclude_tag").forGetter(SpawnItems::excludeTag)
    ).apply(in, SpawnItems::new));

    public boolean canApplyToPlayer(final ServerPlayer player) {
        return excludeTag.map(tag -> !player.getTags().contains(tag)).orElse(true);
    }

    public record Stack(Holder<Item> item, int count, DataComponentPatch components) {
        private static final Codec<Stack> FULL_CODEC = RecordCodecBuilder.create(i -> i.group(
                BuiltInRegistries.ITEM.holderByNameCodec().fieldOf("id").forGetter(Stack::item),
                Codec.INT.optionalFieldOf("count", 1).forGetter(Stack::count),
                DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(Stack::components)
        ).apply(i, Stack::new));

        public static final Codec<Stack> CODEC = Codec.withAlternative(
                FULL_CODEC,
                BuiltInRegistries.ITEM.holderByNameCodec(),
                item -> new Stack(item, 1, DataComponentPatch.EMPTY)
        );

        public ItemStack build() {
            return new ItemStack(item, count, components);
        }
    }
}
