package com.lovetropics.extras.templates.modifiers;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.templates.ItemTemplateModifier;
import com.lovetropics.extras.templates.modifiers.loot.LootItemTemplateModifier;
import com.lovetropics.extras.registry.ExtraRegistries;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * LTExtras
 * FIRE EMOJI, FIRE EMOJI, FIRE EMOJI
 */
public class ExtraItemTemplateModifierTypes {

    public static final DeferredRegister<MapCodec<? extends ItemTemplateModifier>> REGISTER = DeferredRegister.create(ExtraRegistries.ITEM_TEMPLATE_MODIFIER_TYPES, LTExtras.MODID);

    public static final DeferredHolder<MapCodec<? extends ItemTemplateModifier>, MapCodec<LootItemTemplateModifier>> STT_LOOT = REGISTER.register("stt_loot", () -> LootItemTemplateModifier.CODEC);
}
