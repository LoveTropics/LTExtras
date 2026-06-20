package com.lovetropics.extras.entity;

import com.lovetropics.extras.LTExtras;
import net.minecraft.advancements.predicates.BlockPredicate;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Optional;

public class ExtraSerializers {

    public static final DeferredRegister<EntityDataSerializer<?>> REGISTER = DeferredRegister.create(NeoForgeRegistries.ENTITY_DATA_SERIALIZERS, LTExtras.MODID);

    public static final DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<Optional<BlockPredicate>>> BLOCK_PREDICATE = REGISTER.register("block_predicate", () -> EntityDataSerializer.forValueType(ByteBufCodecs.optional(BlockPredicate.STREAM_CODEC)));
}
