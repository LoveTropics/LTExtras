package com.lovetropics.extras.effect;

import com.lovetropics.extras.LTExtras;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = LTExtras.MODID)
public class FishEyeEffect extends MobEffect {
    public FishEyeEffect(MobEffectCategory category, int color) {
        super(category, color);
    }
}
