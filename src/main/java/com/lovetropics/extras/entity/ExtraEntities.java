package com.lovetropics.extras.entity;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.block.entity.SeatEntity;
import com.lovetropics.extras.client.entity.*;
import com.lovetropics.extras.entity.ravekoa.RaveKoaEntity;
import com.lovetropics.extras.entity.ravekoa.RaveKoaEntityDJ;
import com.lovetropics.extras.entity.ravekoa.RaveKoaEntityDance1;
import com.lovetropics.extras.entity.ravekoa.RaveKoaEntityDance2;
import com.lovetropics.extras.entity.vfx.PartyBeamEntity;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.EntityEntry;
import net.minecraft.SharedConstants;
import net.minecraft.world.entity.MobCategory;

public class ExtraEntities {
	public static final Registrate REGISTRATE = LTExtras.registrate();

	public static final EntityEntry<PartyBeamEntity> PARTY_BEAM = REGISTRATE.entity("party_beam", PartyBeamEntity::new, MobCategory.MISC)
			.defaultLang()
			.properties(builder -> builder
					.sized(2.0F, 2.0F)
					.clientTrackingRange(16)
					.updateInterval(4))
			.renderer(() -> PartyBeamRenderer::new)
			.register();

	public static final EntityEntry<CollectibleEntity> COLLECTIBLE = REGISTRATE.entity("collectible", CollectibleEntity::new, MobCategory.MISC)
			.defaultLang()
			.properties(builder -> builder
					.sized(0.8f, 0.8f)
					.clientTrackingRange(2)
					.updateInterval(Integer.MAX_VALUE)
			)
			.renderer(() -> CollectibleEntityRenderer::new)
			.register();

	public static final EntityEntry<HologramEntity> HOLOGRAM = REGISTRATE.entity("hologram", HologramEntity::new, MobCategory.MISC)
			.defaultLang()
			.properties(builder -> builder
					.sized(0.8f, 0.8f)
					.clientTrackingRange(8)
					.updateInterval(SharedConstants.TICKS_PER_SECOND)
			)
			.renderer(() -> HologramEntityRenderer::new)
			.register();

	public static final EntityEntry<RaveKoaEntityDJ> RAVEKOADJ = REGISTRATE.entity("ravekoa_dj", RaveKoaEntityDJ::new, MobCategory.MISC)
			.defaultLang()
			.properties(builder -> builder
					.sized(0.8f, 1.6f)
					.clientTrackingRange(8)
					.updateInterval(SharedConstants.TICKS_PER_SECOND)
			)
			.attributes(RaveKoaEntity::createAttributes)
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
			.renderer(() -> RaveKoaRenderer::new)
			.register();

    public static final EntityEntry<SeatEntity> SEAT = REGISTRATE.entity("seat", SeatEntity::new, MobCategory.MISC)
            .defaultLang()
            .properties(builder -> builder
                    .sized(0.50f, 0.50f)
                    .clientTrackingRange(2)
                    .updateInterval(SharedConstants.TICKS_PER_MINUTE)
            )
            .renderer(() -> SeatRenderer::new)
            .register();

	public static void init() {
	}
}
