package com.lovetropics.extras.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;

public class SelectRandomFunction extends LootItemConditionalFunction {

    public static final MapCodec<SelectRandomFunction> MAP_CODEC = RecordCodecBuilder.mapCodec(i ->
            commonFields(i).and(
                    LootItemFunctions.TYPED_CODEC.listOf().fieldOf("functions").forGetter(f -> f.functions)
            ).apply(i, SelectRandomFunction::new)
    );

    private final List<LootItemFunction> functions;

    public SelectRandomFunction(List<LootItemCondition> predicates, List<LootItemFunction> functions) {
        super(predicates);
        this.functions = functions;
    }

    @Override
    public MapCodec<? extends LootItemConditionalFunction> codec() {
        return MAP_CODEC;
    }

    @Override
    protected ItemStack run(ItemStack itemStack, LootContext context) {
        int i = context.getRandom().nextInt(functions.size());
        return functions.get(i).apply(itemStack, context);
    }
}
