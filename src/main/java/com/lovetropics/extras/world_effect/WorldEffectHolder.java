package com.lovetropics.extras.world_effect;

import net.minecraft.resources.ResourceLocation;

public record WorldEffectHolder(ResourceLocation id, WorldEffect value) {
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof final WorldEffectHolder holder && id.equals(holder.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
