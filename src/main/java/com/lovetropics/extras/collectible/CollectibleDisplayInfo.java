package com.lovetropics.extras.collectible;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record CollectibleDisplayInfo(String name, boolean description, CollectibleRarity rarity, Optional<Component> additionalLore) {

    public static final Codec<CollectibleDisplayInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.fieldOf("name").forGetter(CollectibleDisplayInfo::name),
        Codec.BOOL.optionalFieldOf("description", false).forGetter(CollectibleDisplayInfo::description),
        CollectibleRarity.CODEC.fieldOf("rarity").forGetter(CollectibleDisplayInfo::rarity),
        ComponentSerialization.CODEC.optionalFieldOf("additional_lore").forGetter(CollectibleDisplayInfo::additionalLore)
    ).apply(instance, CollectibleDisplayInfo::new));


    public static final StreamCodec<RegistryFriendlyByteBuf, CollectibleDisplayInfo> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.stringUtf8(1024), CollectibleDisplayInfo::name,
        ByteBufCodecs.BOOL, CollectibleDisplayInfo::description,
        CollectibleRarity.STREAM_CODEC, CollectibleDisplayInfo::rarity,
        ByteBufCodecs.optional(ComponentSerialization.STREAM_CODEC), CollectibleDisplayInfo::additionalLore,
        CollectibleDisplayInfo::new
    );


    public List<Component> getLore() {
        List<Component> lore = new ArrayList<>();
        if (description) {
            lore.add(Component.translatable("lt.collectible." + name + ".description").withStyle(ChatFormatting.GRAY));
        }
        lore.add(Component.empty());
        lore.add(Component.translatable("lt.collectible.unicode." + rarity.name().toLowerCase()));
        additionalLore.ifPresent(lore::add);
        return lore;
    }

}
