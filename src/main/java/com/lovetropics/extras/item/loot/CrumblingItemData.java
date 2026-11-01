package com.lovetropics.extras.item.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record CrumblingItemData(
        Set<EquipmentSlot> inEquipmentSlots,
        float interval,
        int damageAmount,
        boolean inNonEquipmentSlot
) {
    public static final Codec<CrumblingItemData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            NeoForgeExtraCodecs.setOf(EquipmentSlot.CODEC).optionalFieldOf("in_equipment_slots", new HashSet<>()).forGetter(CrumblingItemData::inEquipmentSlots),
            Codec.FLOAT.optionalFieldOf("interval", 5.0f).forGetter(CrumblingItemData::interval),
            Codec.INT.optionalFieldOf("damage_amount", 1).forGetter(CrumblingItemData::damageAmount),
            Codec.BOOL.optionalFieldOf("in_non_equipment_slot", false).forGetter(CrumblingItemData::inNonEquipmentSlot)
    ).apply(instance, CrumblingItemData::new));

    public int intervalTicks() {
        return Math.round(interval * 20);
    }

    public static final CrumblingItemData DEFAULT = new CrumblingItemData(new HashSet<>(), 5.0f, 1, false);
}
