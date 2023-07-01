package com.lovetropics.extras.entity;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.client.entity.PartyBeamRenderer;
import com.lovetropics.extras.entity.vfx.PartyBeamEntity;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.EntityEntry;
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

	public static void init() {
	}
}
