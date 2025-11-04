package com.lovetropics.extras.collectible;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.stream.Stream;

public enum CollectibleRarity implements StringRepresentable {
    PURCHASED("#BFEA00"),
    REWARD("005958"),
    SECRET("#5ABDA3"),
    MILESTONE("#005958"),
    QUEST("#E5B106");

    public static final Codec<CollectibleRarity> CODEC = StringRepresentable.fromEnum(CollectibleRarity::values);
    public static final StreamCodec<FriendlyByteBuf, CollectibleRarity> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(CollectibleRarity.class);

    private final String color;

    CollectibleRarity(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase();
    }
}
