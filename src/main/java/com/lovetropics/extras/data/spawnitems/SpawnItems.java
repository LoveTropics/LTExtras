package com.lovetropics.extras.data.spawnitems;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStackTemplate;

import java.util.List;
import java.util.Optional;

public record SpawnItems(List<ItemStackTemplate> items, boolean canBeRestored, Optional<String> excludeTag) {
    public static final Codec<SpawnItems> CODEC = RecordCodecBuilder.create(in -> in.group(
            ItemStackTemplate.CODEC.listOf().fieldOf("items").forGetter(SpawnItems::items),
            Codec.BOOL.optionalFieldOf("can_be_restored", true).forGetter(SpawnItems::canBeRestored),
            Codec.STRING.optionalFieldOf("exclude_tag").forGetter(SpawnItems::excludeTag)
    ).apply(in, SpawnItems::new));

    public boolean canApplyToPlayer(ServerPlayer player) {
        return excludeTag.map(tag -> !player.entityTags().contains(tag)).orElse(true);
    }
}
