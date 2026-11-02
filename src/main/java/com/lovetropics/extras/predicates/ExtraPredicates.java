package com.lovetropics.extras.predicates;

import com.lovetropics.extras.LTExtras;
import com.mojang.serialization.Codec;
import net.minecraft.advancements.predicates.entity.EntitySubPredicate;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ExtraPredicates {

    public static final DeferredRegister<Codec<? extends EntitySubPredicate>> REGISTER = DeferredRegister.create(Registries.ENTITY_SUB_PREDICATE_TYPE, LTExtras.MODID);

    public static final DeferredHolder<Codec<? extends EntitySubPredicate>, Codec<HasCollectablePredicate>> HAS_COLLECTABLE = REGISTER.register("has_collectable", () -> HasCollectablePredicate.CODEC);
}
