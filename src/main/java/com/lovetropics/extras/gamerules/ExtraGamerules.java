package com.lovetropics.extras.gamerules;

import com.lovetropics.extras.LTExtras;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;
import net.minecraft.world.level.gamerules.GameRuleType;
import net.minecraft.world.level.gamerules.GameRuleTypeVisitor;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ExtraGamerules {

    public static final DeferredRegister<GameRule<?>> REGISTER = DeferredRegister.create(Registries.GAME_RULE, LTExtras.MODID);

    public static final DeferredHolder<GameRule<?>, GameRule<Boolean>> DISABLE_GLOBAL_EQUIP_SOUND = registerBoolean("disable_global_equip_sound", GameRuleCategory.PLAYER, false);

    private static DeferredHolder<GameRule<?>, GameRule<Boolean>> registerBoolean(String id, GameRuleCategory category, boolean defaultValue) {
        return REGISTER.register(id, () -> new GameRule<>(category, GameRuleType.BOOL, BoolArgumentType.bool(), GameRuleTypeVisitor::visitBoolean, Codec.BOOL, b -> b ? 1 : 0, defaultValue, FeatureFlagSet.of()));
    }

    private static DeferredHolder<GameRule<?>, GameRule<Integer>> registerInteger(String id, GameRuleCategory category, int defaultValue, int min) {
        return REGISTER.register(id, () -> new GameRule<>(category, GameRuleType.INT, IntegerArgumentType.integer(min, Integer.MAX_VALUE), GameRuleTypeVisitor::visitInteger, Codec.INT, i -> i, defaultValue, FeatureFlagSet.of()));
    }
}
