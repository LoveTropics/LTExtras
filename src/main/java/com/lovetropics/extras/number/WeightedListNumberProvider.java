package com.lovetropics.extras.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

// TODO: Remove in 26.3
public record WeightedListNumberProvider(
        WeightedList<NumberProvider> distribution
) implements NumberProvider {
    public static final MapCodec<WeightedListNumberProvider> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            WeightedList.nonEmptyCodec(NumberProviders.CODEC).fieldOf("distributions").forGetter(WeightedListNumberProvider::distribution)
    ).apply(i, WeightedListNumberProvider::new));

    @Override
    public float getFloat(LootContext context) {
        return distribution.getRandomOrThrow(context.getRandom()).getFloat(context);
    }

    @Override
    public MapCodec<WeightedListNumberProvider> codec() {
        return CODEC;
    }
}
