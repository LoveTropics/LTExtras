package com.lovetropics.extras.entity;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.block.entity.SeatEntity;
import com.lovetropics.extras.client.entity.CollectibleEntityRenderer;
import com.lovetropics.extras.client.entity.PartyBeamRenderer;
import com.lovetropics.extras.client.entity.RaveKoaRenderer;
import com.lovetropics.extras.client.entity.SeatRenderer;
import com.lovetropics.extras.entity.ravekoa.RaveKoaEntity;
import com.lovetropics.extras.entity.ravekoa.RaveKoaEntityDJ;
import com.lovetropics.extras.entity.ravekoa.RaveKoaEntityDance1;
import com.lovetropics.extras.entity.ravekoa.RaveKoaEntityDance2;
import com.lovetropics.extras.entity.vfx.PartyBeamEntity;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.providers.loot.RegistrateEntityLootTables;
import com.tterrag.registrate.util.entry.EntityEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.SharedConstants;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.storage.loot.LootTable;

public class ExtraEntities {
    public static final Registrate REGISTRATE = LTExtras.registrate();

    private static <T extends Entity> NonNullBiConsumer<RegistrateEntityLootTables, EntityType<T>> noDrops() {
        return (lootTables, type) -> lootTables.add(type, LootTable.lootTable());
    }

    public static final EntityEntry<PartyBeamEntity> PARTY_BEAM = REGISTRATE.entity("party_beam", PartyBeamEntity::new, MobCategory.MISC)
            .defaultLang()
            .properties(builder -> builder
                    .sized(2.0F, 2.0F)
                    .clientTrackingRange(16)
                    .updateInterval(4))
            .loot(noDrops())
            .renderer(() -> PartyBeamRenderer::new)
            .register();

    public static final EntityEntry<CollectibleEntity> COLLECTIBLE = REGISTRATE.entity("collectible", CollectibleEntity::new, MobCategory.MISC)
            .defaultLang()
            .properties(builder -> builder
                    .sized(0.8f, 0.8f)
                    .clientTrackingRange(2)
                    .updateInterval(Integer.MAX_VALUE)
            )
            .loot(noDrops())
            .renderer(() -> CollectibleEntityRenderer::new)
            .register();

    public static final EntityEntry<RaveKoaEntityDJ> RAVEKOADJ = REGISTRATE.entity("ravekoa_dj", RaveKoaEntityDJ::new, MobCategory.MISC)
            .defaultLang()
            .properties(builder -> builder
                    .sized(0.8f, 1.6f)
                    .clientTrackingRange(8)
                    .updateInterval(SharedConstants.TICKS_PER_SECOND)
            )
            .attributes(RaveKoaEntity::createAttributes)
            .loot(noDrops())
            .renderer(() -> RaveKoaRenderer::new)
            .register();

    public static final EntityEntry<RaveKoaEntityDance1> RAVEKOADANCE1 = REGISTRATE.entity("ravekoa_dance1", RaveKoaEntityDance1::new, MobCategory.MISC)
            .defaultLang()
            .properties(builder -> builder
                    .sized(0.8f, 1.6f)
                    .clientTrackingRange(8)
                    .updateInterval(SharedConstants.TICKS_PER_SECOND)
            )
            .attributes(RaveKoaEntity::createAttributes)
            .loot(noDrops())
            .renderer(() -> RaveKoaRenderer::new)
            .register();

    public static final EntityEntry<RaveKoaEntityDance2> RAVEKOADANCE2 = REGISTRATE.entity("ravekoa_dance2", RaveKoaEntityDance2::new, MobCategory.MISC)
            .defaultLang()
            .properties(builder -> builder
                    .sized(0.8f, 1.6f)
                    .clientTrackingRange(8)
                    .updateInterval(SharedConstants.TICKS_PER_SECOND)
            )
            .attributes(RaveKoaEntity::createAttributes)
            .loot(noDrops())
            .renderer(() -> RaveKoaRenderer::new)
            .register();

    public static final EntityEntry<SeatEntity> SEAT = REGISTRATE.entity("seat", SeatEntity::new, MobCategory.MISC)
            .defaultLang()
            .properties(builder -> builder
                    .sized(0.50f, 0.50f)
                    .clientTrackingRange(2)
                    .updateInterval(SharedConstants.TICKS_PER_MINUTE)
            )
            .loot(noDrops())
            .renderer(() -> SeatRenderer::new)
            .register();

    public static void init() {
    }
}
