package com.lovetropics.extras.world_effect;

import com.lovetropics.extras.network.LTExtrasNetwork;
import com.lovetropics.extras.network.SetSkyColorPacket;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.SharedConstants;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.FastColor;
import net.minecraftforge.network.PacketDistributor;

public record SkyColorEffect(int red, int green, int blue, int fadeLength) implements WorldEffect {
    private static final Codec<Integer> COLOR_COMPONENT_CODEC = ExtraCodecs.intRange(0, 255);

    public static final MapCodec<SkyColorEffect> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            COLOR_COMPONENT_CODEC.fieldOf("red").forGetter(SkyColorEffect::red),
            COLOR_COMPONENT_CODEC.fieldOf("green").forGetter(SkyColorEffect::green),
            COLOR_COMPONENT_CODEC.fieldOf("blue").forGetter(SkyColorEffect::blue),
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("fade_length", SharedConstants.TICKS_PER_SECOND).forGetter(SkyColorEffect::fadeLength)
    ).apply(i, SkyColorEffect::new));

    @Override
    public void apply(final ServerPlayer player, final boolean immediate) {
        final int color = FastColor.ARGB32.color(0, red, green, blue);
        LTExtrasNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new SetSkyColorPacket(color, immediate ? 0 : fadeLength));
    }

    @Override
    public void clear(final ServerPlayer player, final boolean immediate) {
        LTExtrasNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), SetSkyColorPacket.clear(fadeLength));
    }

    @Override
    public WorldEffectType type() {
        return WorldEffectType.SKY_COLOR;
    }
}
