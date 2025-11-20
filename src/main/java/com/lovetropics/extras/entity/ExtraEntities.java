package com.lovetropics.extras.entity;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.block.entity.SeatEntity;
import com.lovetropics.extras.client.entity.AmazonRiverDolphinRenderer;
import com.lovetropics.extras.client.entity.CleaningItemFrameRender;
import com.lovetropics.extras.client.entity.CollectibleEntityRenderer;
import com.lovetropics.extras.client.entity.FallingPropaguleRenderer;
import com.lovetropics.extras.client.entity.ForkliftRenderer;
import com.lovetropics.extras.client.entity.GlassFrogRenderer;
import com.lovetropics.extras.client.entity.PartyBeamRenderer;
import com.lovetropics.extras.client.entity.RaveKoaRenderer;
import com.lovetropics.extras.client.entity.SeatRenderer;
import com.lovetropics.extras.client.entity.SpinningSignRenderer;
import com.lovetropics.extras.client.entity.WaterCoolerRenderer;
import com.lovetropics.extras.entity.glass_frog.GlassFrog;
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
import net.minecraft.client.renderer.entity.TntRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.Vec3;

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

    public static final EntityEntry<ForkliftEntity> FORKLIFT = REGISTRATE.entity("forklift", ForkliftEntity::new, MobCategory.MISC)
            .defaultLang()
            .properties(builder -> builder
                    .sized(1.4f, 2.1f)
                    .clientTrackingRange(8)
                    .passengerAttachments(new Vec3(0.0f, 0.8f, 0.0f), new Vec3(1.0f, 1.0f, 1.0f), new Vec3(-1.0f, 1.0f, 1.0f))
                    .updateInterval(3)
            )
            .loot(noDrops())
            .renderer(() -> ForkliftRenderer::new)
            .register();

    public static final EntityEntry<WaterCoolerEntity> WATER_COOLER = REGISTRATE.entity("water_cooler", WaterCoolerEntity::new, MobCategory.MISC)
            .defaultLang()
            .properties(builder -> builder
                    .sized(1.4f, 2.1f)
                    .passengerAttachments(new Vec3(0.0f, 0.8f, 0.0f), new Vec3(1.0f, 1.0f, 1.0f), new Vec3(-1.0f, 1.0f, 1.0f))
                    .updateInterval(3)
            )
            .loot(noDrops())
            .renderer(() -> WaterCoolerRenderer::new)
            .register();

    public static final EntityEntry<AmazonRiverDolphin> AMAZON_RIVER_DOLPHIN = REGISTRATE.entity("amazon_river_dolphin", AmazonRiverDolphin::new, MobCategory.MISC)
            .defaultLang()
            .properties(builder -> builder
                    .sized(0.9F, 0.6F)
                    .eyeHeight(0.3F)
            )
            .loot(noDrops())
            .renderer(() -> AmazonRiverDolphinRenderer::new)
            .attributes(AmazonRiverDolphin::createAttributes)
            .register();

    public static final EntityEntry<GlassFrog> GLASS_FROG = REGISTRATE.entity("glass_frog", GlassFrog::new, MobCategory.CREATURE)
            .defaultLang()
            .properties(builder -> builder
                    .sized(0.3F, 0.15F)
                    .passengerAttachments(new Vec3(0.0, 0.25, -0.25))
            )
            .loot(noDrops())
            .renderer(() -> GlassFrogRenderer::new)
            .attributes(GlassFrog::createAttributes)
            .register();

    public static final EntityEntry<CleaningItemFrame> CLEANING_ITEM_FRAME = REGISTRATE.entity("cleaning_item_frame", CleaningItemFrame::new, MobCategory.MISC)
            .defaultLang()
            .properties(builder -> builder
                    .noLootTable()
                    .sized(0.5F, 0.5F)
                    .eyeHeight(0.0F)
                    .updateInterval(Integer.MAX_VALUE)
            )
            .renderer(() -> CleaningItemFrameRender::new)
            .register();

    public static final EntityEntry<FallingPropagule> FALLING_PROPAGULE = REGISTRATE.entity("falling_propagule", FallingPropagule::new, MobCategory.MISC)
            .defaultLang()
            .properties(builder -> builder
                    .noLootTable()
                    .sized(0.2F, 0.7F)
                    .eyeHeight(0.0F)
                    .updateInterval(3)
            )
            .renderer(() -> FallingPropaguleRenderer::new)
            .register();

    public static final EntityEntry<SpinningSignEntity> SPINNING_SIGN = REGISTRATE.entity("spinning_sign", SpinningSignEntity::new, MobCategory.MISC)
            .defaultLang()
            .properties(builder -> builder
                    .noLootTable()
                    .sized(3F, 1F)
                    .eyeHeight(0.0F)
                    .clientTrackingRange(10)
                    .updateInterval(3)
            )
            .renderer(() -> SpinningSignRenderer::new)
            .register();

    public static final EntityEntry<PrimedPlumbersTnt> PRIMED_PLUMBERS_TNT = REGISTRATE.entity("primed_plumbers_tnt", PrimedPlumbersTnt::new, MobCategory.MISC)
            .lang("Primed Plumbers' TNT")
            .properties(builder -> builder
                    .noLootTable()
                    .fireImmune()
                    .sized(0.98F, 0.98F)
                    .eyeHeight(0.15F)
                    .updateInterval(10)
            )
            .renderer(() -> TntRenderer::new)
            .register();

    public static void init() {
    }
}
