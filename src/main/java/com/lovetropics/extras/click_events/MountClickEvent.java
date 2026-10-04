package com.lovetropics.extras.click_events;

import com.lovetropics.extras.data.Named;
import com.lovetropics.extras.mounts.Mount;
import com.lovetropics.extras.mounts.MountConfigs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Consumer;

public record MountClickEvent(Named<Mount> mount) implements ExtraClickEvent {

    public static final MapCodec<MountClickEvent> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            MountConfigs.MOUNTS.fieldOf("mount").forGetter(MountClickEvent::mount)
    ).apply(i, MountClickEvent::new));

    @Override
    public void handleAction(ServerPlayer serverPlayer, Tag tag, Consumer<Component> errorHandler) {
        if (!mount.value().spawnFor(serverPlayer, serverPlayer.level())) {
            errorHandler.accept(Component.literal("Failed to spawn mount entity."));
        }
    }

    @Override
    public MapCodec<? extends ExtraClickEvent> getCodec() {
        return CODEC;
    }
}
