package com.lovetropics.extras.collectible;

import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.List;
import java.util.function.Function;

public enum CollectibleExtraDisplayType implements StringRepresentable {
    NONE(Component.empty(), type -> List.of()),
    HAT(Component.translatable("lt.collectible.disguise.hat.suffix"), type -> List.of(
            Component.literal(" +").append(type.getDescription())
                    .withStyle(ChatFormatting.BLUE))),
    DISGUISE(Component.translatable("lt.collectible.disguise.name.suffix"), type ->
            List.of(
                    Component.translatable("lt.collectible.disguise.tooltip.subtract_player"),
                    Component.literal(" +").append(type.getDescription())
                            .withStyle(ChatFormatting.BLUE)
            )),
    ;

    private final Component component;
    private final Function<EntityType<?>, List<Component>> builder;

    CollectibleExtraDisplayType(Component component, Function<EntityType<?>, List<Component>> builder) {
        this.component = component;
        this.builder = builder;
    }

    public Component getComponent() {
        return component;
    }

    public List<Component> getAdditionalLore(EntityType<?> type) {
        return builder.apply(type);
    }

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase();
    }
}
