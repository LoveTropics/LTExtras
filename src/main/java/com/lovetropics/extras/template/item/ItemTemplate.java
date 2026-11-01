package com.lovetropics.extras.template.item;

import com.lovetropics.extras.data.Named;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.FunctionReference;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * LTExtras
 * FIRE EMOJI, FIRE EMOJI, FIRE EMOJI
 */
public class ItemTemplate {

    // TODO[26.3]: Use this instead, as we can be hooked up better into normal registry loading where we can reference unbound holders during load
//    private static final Codec<HolderSet<LootItemFunction>> MODIFIERS_CODEC = RegistryCodecs.homogeneousList(Registries.ITEM_MODIFIER, LootItemFunctions.TYPED_CODEC);
    private static final Codec<HolderSet<LootItemFunction>> MODIFIERS_CODEC = Codec.either(ResourceKey.codec(Registries.ITEM_MODIFIER), LootItemFunctions.ROOT_CODEC).listOf().<HolderSet<LootItemFunction>>xmap(
            eithers -> HolderSet.direct(eithers.stream()
                    .map(e -> e.map(i -> FunctionReference.functionReference(i).build(), Function.identity()))
                    .map(Holder::direct)
                    .toList()),
            // Not encoding reference for simplicity - doesn't matter for us
            holders -> holders.stream()
                    .map(h -> Either.<ResourceKey<LootItemFunction>, LootItemFunction>right(h.value()))
                    .toList()
    );

    public static final Codec<ItemTemplate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStackTemplate.CODEC.fieldOf("item").forGetter(template -> template.item),
            MODIFIERS_CODEC.optionalFieldOf("modifiers", HolderSet.empty()).forGetter(template -> template.functions)
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

    public static LootTable asLootTable(Named<ItemTemplate> template) {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(ItemTemplateLootEntry.builder(template.id()))
                )
                .build();
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
