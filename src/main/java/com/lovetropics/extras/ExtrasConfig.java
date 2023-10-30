package com.lovetropics.extras;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@EventBusSubscriber(modid = LTExtras.MODID, bus = Bus.MOD)
public class ExtrasConfig {
    private static final Builder COMMON_BUILDER = new Builder();

    public static final CategoryTechStack TECH_STACK = new CategoryTechStack();

    public static final class CategoryTechStack {
        public final ConfigValue<String> authKey;
        public final ConfigValue<String> scheduleUrl;
        public final ConfigValue<String> translationUrl;

        private CategoryTechStack() {
            COMMON_BUILDER.comment("Connection to the tech stack").push("techStack");

            authKey = COMMON_BUILDER
                    .comment("API Key used to allow authentication with the tech stack")
                    .define("authKey", "");

            scheduleUrl = COMMON_BUILDER
                    .comment("API URL to get stream schedule from")
                    .define("schedule", "http://localhost/schedule");

            translationUrl = COMMON_BUILDER
                    .comment("API URL to request translations from")
                    .define("translationUrl", "");

            COMMON_BUILDER.pop();
        }
    }

    public static final ForgeConfigSpec COMMON_CONFIG = COMMON_BUILDER.build();

    @SubscribeEvent
	public static void configLoad(final ModConfigEvent.Loading event) {
	}

	@SubscribeEvent
	public static void configReload(final ModConfigEvent.Reloading event) {
	}
}
