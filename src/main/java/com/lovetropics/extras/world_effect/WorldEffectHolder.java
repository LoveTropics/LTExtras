package com.lovetropics.extras.world_effect;

import net.minecraft.resources.ResourceLocation;

public record WorldEffectHolder(ResourceLocation id, WorldEffect value) {
    @Override
    public boolean equals(Object obj) {
        return obj instanceof WorldEffectHolder holder && id.equals(holder.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
