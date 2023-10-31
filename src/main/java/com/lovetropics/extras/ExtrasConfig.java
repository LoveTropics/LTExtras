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
    private static final Builder CLIENT_BUILDER = new Builder();

    public static final CategoryCommands COMMANDS = new CategoryCommands();
    public static final CategoryTranslation TRANSLATION = new CategoryTranslation();
    public static final CategoryTechStack TECH_STACK = new CategoryTechStack();

    public static final class CategoryCommands {
        public final ConfigValue<String> tpaDimension;

        private CategoryCommands() {
            COMMON_BUILDER.comment("Commands").push("commands");

            tpaDimension = COMMON_BUILDER
                    .comment("If not blank, the /tpa command will only be allowed in this dimension")
                    .define("tpaDimension", "tropicraft:tropics");

            COMMON_BUILDER.pop();
        }
    }

    public static final class CategoryTranslation {
        public final ConfigValue<Boolean> translateOutgoing;
        public final ConfigValue<Boolean> translateIncoming;
        public final ConfigValue<Boolean> prompted;

        private CategoryTranslation() {
            CLIENT_BUILDER.comment("Translation").push("translation");

            translateOutgoing = CLIENT_BUILDER
                    .comment("True if messages that you send should be translated for other players")
                    .define("translateOutgoing", true);

            translateIncoming = CLIENT_BUILDER
                    .comment("True if messages that others send should be translated for you")
                    .define("translateIncoming", true);

            prompted = CLIENT_BUILDER
                    .comment("True if the player has been prompted to select translation options yet")
                    .define("prompted", false);

            CLIENT_BUILDER.pop();
        }
    }

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
    public static final ForgeConfigSpec CLIENT_CONFIG = CLIENT_BUILDER.build();

    @SubscribeEvent
    public static void configLoad(final ModConfigEvent.Loading event) {
    }

    @SubscribeEvent
    public static void configReload(final ModConfigEvent.Reloading event) {
    }
}
