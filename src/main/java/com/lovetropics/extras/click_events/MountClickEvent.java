package com.lovetropics.extras.click_events;

import com.lovetropics.extras.mounts.ExtraMountController;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.commands.SummonCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.Optional;
import java.util.function.Consumer;

public record MountClickEvent(ResourceKey<EntityType<?>> type, Optional<CompoundTag> nbt) implements ExtraClickEvent {

    public static final MapCodec<MountClickEvent> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ResourceKey.codec(Registries.ENTITY_TYPE).fieldOf("type").forGetter(MountClickEvent::type),
            CompoundTag.CODEC.optionalFieldOf("nbt").forGetter(MountClickEvent::nbt)
    ).apply(i, MountClickEvent::new));

    @Override
    public void handleAction(ServerPlayer serverPlayer, Tag tag, Consumer<Component> errorHandler) {
        Optional<Holder.Reference<EntityType<?>>> entityTypeReference = serverPlayer.registryAccess().get(type);
        if (entityTypeReference.isEmpty()) {
            errorHandler.accept(Component.literal("Entity type not found in registry: " + type.identifier()));
            return;
        }
        Holder.Reference<EntityType<?>> holder = entityTypeReference.get();
        try {
            Entity spawnEntity = SummonCommand.createEntity(serverPlayer.createCommandSourceStackForNameResolution(serverPlayer.level()), holder, serverPlayer.position(), nbt.orElse(new CompoundTag()), false);
            serverPlayer.startRiding(spawnEntity, true, false);
            spawnEntity.addTag(ExtraMountController.KILL_DISMOUNT);
        }catch (CommandSyntaxException e) {
            errorHandler.accept(Component.literal("Failed to summon entity: " + e.getMessage()));
        }

    }

    @Override
    public MapCodec<? extends ExtraClickEvent> getCodec() {
        return CODEC;
    }
}
