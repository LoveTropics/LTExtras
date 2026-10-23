package com.lovetropics.extras.templates.modifiers.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;

import java.util.HashMap;
import java.util.Map;

public record LootCategory(
    String name,
    TextColor color,
    int tiers
) {
    public static final Codec<LootCategory> CODEC = Codec.STRING.comapFlatMap(name -> DataResult.success(getCategory(name)), LootCategory::name);

    private static final Map<String, LootCategory> REGISTRY = new HashMap<>();

    public static final LootCategory AMMO = register("ammo", "#C7D718", 0);
    public static final LootCategory ARMOUR = register("armour", "#858585", 3);
    public static final LootCategory FOOD = register("food", "#AD6711", 0);
    public static final LootCategory HEALING = register("healing", "#DB3455", 0);
    public static final LootCategory UNIQUE = register("unique", "#AE34DB", 0);
    public static final LootCategory UTILITY = register("utility", "#3FA5D0", 0);
    public static final LootCategory WEAPON = register("weapon", "#E5622A", 3);

    public static final LootCategory DEFAULT = UTILITY;

    private static LootCategory getCategory(String name) {
        return REGISTRY.getOrDefault(name, DEFAULT);
    }

    private static LootCategory register(String name, String color, int tiers) {
        LootCategory newCategory = new LootCategory(name, TextColor.parseColor(color).getOrThrow(), tiers);
        REGISTRY.put(name, newCategory);
        return newCategory;
    }

    // Divider comment for funsies

    public MutableComponent getTranslatedIcon(int withTier) {
        int tier = clampTier(withTier);
        if (clampTier(tier) > 0) {
            // white because the icon unicode characters are already colored.
            return getLang(name + ".icon." + tier, TextColor.WHITE);
        }
        return getLang(name + ".icon", TextColor.WHITE);
    }

    public MutableComponent getTranslatedLozenge() {
        return getLang(name, TextColor.WHITE);
    }

    public MutableComponent getName() {
        return getLang(name + ".name", color);
    }

    private int clampTier(int desiredTier) {
        return Math.clamp(desiredTier, 0, tiers);
    }

    public TextColor getColor() {
        return color;
    }

    private static MutableComponent getLang(String name, TextColor color) {
        return Component.translatable("lt.loot_category." + name).withColor(color);
    }
}
