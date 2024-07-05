package com.lovetropics.extras.network.message;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.client.world_effect.WorldParticleEffectHandler;
import com.lovetropics.extras.world_effect.ParticlesEffect;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Optional;

public record ClientboundWorldParticleEffectsPacket(Optional<ParticlesEffect> effect) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundWorldParticleEffectsPacket> STREAM_CODEC = StreamCodec.composite(
            ParticlesEffect.STREAM_CODEC.apply(ByteBufCodecs::optional), ClientboundWorldParticleEffectsPacket::effect,
            ClientboundWorldParticleEffectsPacket::new
    );

    public static final Type<ClientboundWorldParticleEffectsPacket> TYPE = new Type<>(LTExtras.location("world_particle_effects"));

    public static void handle(final ClientboundWorldParticleEffectsPacket packet, final IPayloadContext ctx) {
        WorldParticleEffectHandler.set(packet.effect.orElse(null));
    }

    @Override
    public Type<ClientboundWorldParticleEffectsPacket> type() {
        return TYPE;
    }
}
