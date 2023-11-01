package com.lovetropics.extras.data.spawnitems;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public record SpawnItems(List<Stack> items, boolean canBeRestored, Optional<String> excludeTag) {
    public static final Codec<SpawnItems> CODEC = RecordCodecBuilder.create(in -> in.group(
            Stack.OR_ID.listOf().fieldOf("items").forGetter(SpawnItems::items),
            Codec.BOOL.optionalFieldOf("can_be_restored", true).forGetter(SpawnItems::canBeRestored),
            Codec.STRING.optionalFieldOf("exclude_tag").forGetter(SpawnItems::excludeTag)
    ).apply(in, SpawnItems::new));

    public boolean shouldApplyToPlayer(final ServerPlayer player) {
        return excludeTag.map(tag -> !player.getTags().contains(tag)).orElse(true);
    }

    public record Stack(Item item, int count, Optional<CompoundTag> tag) {
        public static final Codec<Stack> CODEC = RecordCodecBuilder.create(i -> i.group(
                BuiltInRegistries.ITEM.byNameCodec().fieldOf("id").forGetter(Stack::item),
                Codec.INT.optionalFieldOf("count", 1).forGetter(Stack::count),
                CompoundTag.CODEC.optionalFieldOf("tag").forGetter(Stack::tag)
        ).apply(i, Stack::new));

        public static final Codec<Stack> OR_ID = Codec.either(CODEC, ResourceLocation.CODEC)
                .xmap(e -> e.map(Function.identity(), rl -> new Stack(
                        BuiltInRegistries.ITEM.get(rl), 1, Optional.empty()
                )), Either::left);

        public ItemStack build() {
            final var stack = new ItemStack(item, count);
            tag.ifPresent(stack::setTag);
            return stack;
        }
    }
}
