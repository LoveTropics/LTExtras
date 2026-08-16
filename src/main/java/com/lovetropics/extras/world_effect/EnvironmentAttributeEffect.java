package com.lovetropics.extras.world_effect;

import com.lovetropics.extras.network.message.ClientboundSetEnvironmentAttributePacket;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.SharedConstants;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Optional;

public record EnvironmentAttributeEffect(EnvironmentAttributeMap value, int fadeLength) implements WorldEffect {

    public static final MapCodec<EnvironmentAttributeEffect> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            EnvironmentAttributeMap.NETWORK_CODEC.fieldOf("value").forGetter(EnvironmentAttributeEffect::value),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("fade_length", SharedConstants.TICKS_PER_SECOND).forGetter(EnvironmentAttributeEffect::fadeLength)
    ).apply(i, EnvironmentAttributeEffect::new));

    @Override
    public void apply(ServerPlayer player, boolean immediate) {
        PacketDistributor.sendToPlayer(player, new ClientboundSetEnvironmentAttributePacket(Optional.of(value), immediate ? 0 : fadeLength));
    }

    @Override
    public void clear(ServerPlayer player, boolean immediate) {
        PacketDistributor.sendToPlayer(player, ClientboundSetEnvironmentAttributePacket.clear(fadeLength));
    }

    @Override
    public WorldEffectType type() {
        return WorldEffectType.SKY_COLOR;
    }
}
