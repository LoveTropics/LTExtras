package com.lovetropics.extras.templates;

import com.lovetropics.extras.registry.ExtraRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

/**
 * LTExtras
 * FIRE EMOJI, FIRE EMOJI, FIRE EMOJI
 */
public interface ItemTemplateModifier {
    Codec<ItemTemplateModifier> CODEC = ExtraRegistries.ITEM_TEMPLATE_MODIFIER_TYPES.byNameCodec().dispatch(ItemTemplateModifier::codec, Function.identity());

    MapCodec<? extends ItemTemplateModifier> codec();

    void apply(ItemStack itemStack);
}
