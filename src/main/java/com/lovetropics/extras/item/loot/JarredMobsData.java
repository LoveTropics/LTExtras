package com.lovetropics.extras.item.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.component.TypedEntityData;

import java.util.List;

/**
 * LTExtras
 * FIRE EMOJI, FIRE EMOJI, FIRE EMOJI
 */
public record JarredMobsData(
        int spawnCount,
        float spread,
        List<TypedEntityData<EntityType<?>>> entities
) {
    public static final Codec<JarredMobsData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("spawn_count", 1).forGetter(JarredMobsData::spawnCount),
            Codec.FLOAT.optionalFieldOf("spread", (float) 0.2).forGetter(JarredMobsData::spread),
            TypedEntityData.codec(EntityType.CODEC).listOf().fieldOf("entities").forGetter(JarredMobsData::entities)
    ).apply(instance, JarredMobsData::new));

    public static JarredMobsData getEmpty() {
        return new JarredMobsData(1, (float) 0.2, List.of());
    }

}
