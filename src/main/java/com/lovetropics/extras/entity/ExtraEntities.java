package com.lovetropics.extras.entity;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.entity.vfx.PartyBeamEntity;
import com.tterrag.registrate.Registrate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ExtraEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, LTExtras.MODID);

    public static final RegistryObject<EntityType<PartyBeamEntity>> PARTY_BEAM = register("party_beam", ExtraEntities::partyBeam);

    private static <E extends Entity, T extends EntityType<E>> RegistryObject<EntityType<E>> register(final String name, final Supplier<EntityType.Builder<E>> sup) {
        return ENTITIES.register(name, () -> sup.get().build(name));
    }

    private static EntityType.Builder<PartyBeamEntity> partyBeam() {
        return EntityType.Builder.of(PartyBeamEntity::new, MobCategory.MISC)
                .sized(2.0F, 2.0F)
                .clientTrackingRange(16)
                .updateInterval(4);
    }
}
