package com.lovetropics.extras.template.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * LTExtras
 * FIRE EMOJI, FIRE EMOJI, FIRE EMOJI
 */
public class ItemTemplate {

    public static final Codec<ItemTemplate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStackTemplate.CODEC.fieldOf("item").forGetter(template -> template.item),
            RegistryCodecs.homogeneousList(Registries.ITEM_MODIFIER, LootItemFunctions.TYPED_CODEC).optionalFieldOf("modifiers", HolderSet.empty()).forGetter(template -> template.functions)
    ).apply(instance, ItemTemplate::new));

    private final ItemStackTemplate item;
    private final HolderSet<LootItemFunction> functions;

    public ItemTemplate(ItemStackTemplate item, HolderSet<LootItemFunction> functions) {
        this.item = item;
        this.functions = functions;
    }

    public ItemStack createItemStack(LootContext context) {
        ItemStack itemStack = item.create();
        for (Holder<LootItemFunction> modifier : functions) {
            itemStack = modifier.value().apply(itemStack, context);
        }
        return itemStack;
    }

    public ItemStack createItemStack(ServerPlayer player) {
        LootContext lootContext = new LootContext.Builder(
                new LootParams.Builder(player.level())
                        .withParameter(LootContextParams.ORIGIN, player.position())
                        .withParameter(LootContextParams.THIS_ENTITY, player)
                        .create(LootContextParamSets.EMPTY)
        ).create(Optional.empty());

        return createItemStack(lootContext);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj instanceof ItemTemplate template) {
            return Objects.equals(this.item, template.item) && Objects.equals(this.functions, template.functions);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(item, functions);
    }
}
