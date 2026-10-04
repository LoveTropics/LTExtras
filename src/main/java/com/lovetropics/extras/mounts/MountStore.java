package com.lovetropics.extras.mounts;

import com.lovetropics.extras.data.attachment.ExtraAttachments;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.Entity;

public record MountStore() {

    public static final MapCodec<MountStore> MAP_CODEC = MapCodec.unit(MountStore::new);

    public static void markMount(Entity entity) {
        entity.setData(ExtraAttachments.MOUNT, new MountStore());
    }
}
