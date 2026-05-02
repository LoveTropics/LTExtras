package com.lovetropics.extras.datagen;

import com.lovetropics.extras.model_modifer.ExtraModelModifiers;
import com.lovetropics.extras.model_modifer.ModelModifier;
import com.lovetropics.extras.model_modifer.types.EnderArmsType;
import com.lovetropics.extras.model_modifer.types.FabulousType;
import com.lovetropics.extras.model_modifer.types.FlailType;
import com.lovetropics.extras.model_modifer.types.HopWalkType;
import com.lovetropics.extras.model_modifer.types.HoveringType;
import com.lovetropics.extras.model_modifer.types.ScaleType;
import com.lovetropics.extras.model_modifer.types.ShruggyArmsType;
import com.lovetropics.extras.model_modifer.types.ShuffleType;
import com.lovetropics.extras.model_modifer.types.StiffLegsType;
import com.lovetropics.extras.model_modifer.types.UpsidedownType;
import com.lovetropics.extras.registry.ExtraRegistries;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(Dist.CLIENT)
public class ModelModifierDataGen {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent.Client event) {
        event.createDatapackRegistryObjects(new RegistrySetBuilder()
                .add(ExtraRegistries.MODEL_MODIFIER, ModelModifierDataGen::generateModelModifiers));
    }

    private static void generateModelModifiers(BootstrapContext<ModelModifier<?>> context) {
        context.register(ExtraModelModifiers.FABULOUS, FabulousType.INSTANCE);
        context.register(ExtraModelModifiers.FLAIL, FlailType.INSTANCE);
        context.register(ExtraModelModifiers.HOVERING, HoveringType.INSTANCE);
        context.register(ExtraModelModifiers.SHUFFLE, ShuffleType.INSTANCE);
        context.register(ExtraModelModifiers.UPSIDEDOWN, UpsidedownType.INSTANCE);
        context.register(ExtraModelModifiers.SHRUNK, new ScaleType.Data(1.0f, 0.8f, 1.0f));
        context.register(ExtraModelModifiers.ENLARGED, new ScaleType.Data(1.0f, 1.2f, 1.0f));
        context.register(ExtraModelModifiers.SHRUGGY_ARMS, ShruggyArmsType.INSTANCE);
        context.register(ExtraModelModifiers.ENDER_ARMS, EnderArmsType.INSTANCE);
        context.register(ExtraModelModifiers.STIFF_LEGS, StiffLegsType.INSTANCE);
        context.register(ExtraModelModifiers.HOP_WALK, HopWalkType.INSTANCE);
    }
}
