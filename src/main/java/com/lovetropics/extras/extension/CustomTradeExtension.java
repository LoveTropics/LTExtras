package com.lovetropics.extras.extension;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.trading.VillagerTrade;

import java.util.Optional;

public record CustomTradeExtension(
        Optional<HolderSet<VillagerTrade>> trade
) {

    public static final CustomTradeExtension EMPTY = new CustomTradeExtension(Optional.empty());

    public static final MapCodec<CustomTradeExtension> TRADE_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            RegistryCodecs.homogeneousList(Registries.VILLAGER_TRADE).optionalFieldOf("trade").forGetter(CustomTradeExtension::trade)
    ).apply(instance, CustomTradeExtension::new));
}
