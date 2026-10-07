package com.lovetropics.extras;

import com.tterrag.registrate.providers.RegistrateLangProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;
import net.minecraft.util.Util;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber
public class ExtraStats {
    public static final DeferredRegister<Identifier> REGISTER = DeferredRegister.create(Registries.CUSTOM_STAT, LTExtras.MODID);

    public static final DeferredHolder<Identifier, Identifier> ZIPLINE_ONE_CM = REGISTER.register("zipline_one_cm", id -> id);

    // Like vanilla, create the stats up front so that they are not created with the default formatter when first awarded
    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> Stats.CUSTOM.get(ZIPLINE_ONE_CM.get(), StatFormatter.DISTANCE));
    }

    public static void addTranslations(RegistrateLangProvider provider) {
        provider.add(Util.makeDescriptionId("stat", ZIPLINE_ONE_CM.getId()), "Distance by Zipline");
    }
}
