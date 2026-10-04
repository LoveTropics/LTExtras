package com.lovetropics.extras.consume_actions;

import com.lovetropics.extras.data.Named;
import com.lovetropics.extras.mounts.Mount;
import com.lovetropics.extras.mounts.MountConfigs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public record MountConsumeAction(Named<Mount> mount) implements ConsumeAction {

    public static final MapCodec<MountConsumeAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            MountConfigs.MOUNTS.fieldOf("mount").forGetter(MountConsumeAction::mount)
    ).apply(i, MountConsumeAction::new));


    @Override
    public void onConsume(ServerPlayer serverPlayer, ItemStack itemStack) {
        mount.value().spawnFor(serverPlayer, serverPlayer.level());
    }

    @Override
    public MapCodec<? extends ConsumeAction> getCodec() {
        return CODEC;
    }
}
