package com.lovetropics.extras.predicates;

import com.lovetropics.extras.collectible.Collectible;
import com.lovetropics.extras.collectible.CollectibleStore;
import com.lovetropics.extras.registry.ExtraRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.predicates.entity.EntitySubPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public record HasCollectablePredicate(HolderSet<Collectible> collectibles) implements EntitySubPredicate {

    public static final Codec<HasCollectablePredicate> CODEC = RecordCodecBuilder.create(i -> i.group(
            RegistryCodecs.homogeneousList(ExtraRegistries.COLLECTIBLE).fieldOf("collectibles").forGetter(HasCollectablePredicate::collectibles)
    ).apply(i, HasCollectablePredicate::new));

    @Override
    public boolean matches(Entity entity, ServerLevel level, @Nullable Vec3 position) {
        if (!(entity instanceof ServerPlayer player)) {
            return false;
        }

        CollectibleStore collectibleStore = CollectibleStore.get(player);
        for (Holder<Collectible> collectible : collectibles) {
            if (!collectibleStore.contains(collectible)) {
                return false;
            }
        }
        return true;
    }
}
