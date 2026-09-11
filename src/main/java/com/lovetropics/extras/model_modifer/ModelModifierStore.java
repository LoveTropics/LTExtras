package com.lovetropics.extras.model_modifer;

import com.lovetropics.extras.data.attachment.ExtraAttachments;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public record ModelModifierStore(List<Holder<ModelModifier<?>>> appliedModifiers) {
    public static final MapCodec<ModelModifierStore> MAP_CODEC = Codec.list(ExtraModelModifiers.REGISTRY_CODEC)
            .xmap(ModelModifierStore::of, ModelModifierStore::appliedModifiers)
            .fieldOf("applied_modifiers");

    public static final StreamCodec<RegistryFriendlyByteBuf, ModelModifierStore> STREAM_CODEC = StreamCodec.composite(
            ExtraModelModifiers.STREAM_CODEC.apply(ByteBufCodecs.list()), ModelModifierStore::appliedModifiers,
            ModelModifierStore::of);

    public static ModelModifierStore of(List<Holder<ModelModifier<?>>> modifiers) {
        return new ModelModifierStore(new ArrayList<>(modifiers));
    }

    public static void addModifier(Entity entity, Holder<ModelModifier<?>> modifier) {
        ModelModifierStore store = getOrDefault(entity);
        /*if (store.appliedModifiers.contains(modifier)) {
            return;
        }*/
        store.appliedModifiers.add(modifier);
        entity.syncData(ExtraAttachments.MODEL_MODIFIERS);
    }

    public static void removeModifier(Entity entity, Holder<ModelModifier<?>> modifier) {
        ModelModifierStore store = getOrDefault(entity);
        if(store.appliedModifiers.remove(modifier)) {
            entity.syncData(ExtraAttachments.MODEL_MODIFIERS);
        }
    }

    public static void resetModifiers(Entity entity) {
        ModelModifierStore store = getOrDefault(entity);
        if (!store.appliedModifiers.isEmpty()) {
            store.appliedModifiers.clear();
            entity.syncData(ExtraAttachments.MODEL_MODIFIERS);
        }
    }

    public static ModelModifierStore getOrDefault(Entity entity) {
        return entity.getData(ExtraAttachments.MODEL_MODIFIERS);
    }
}
