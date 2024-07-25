package com.lovetropics.extras;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = LTExtras.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ExtrasConfig {
    private static final ModConfigSpec.Builder COMMON_BUILDER = new ModConfigSpec.Builder();
    private static final ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();

    public static final CategoryCommands COMMANDS = new CategoryCommands();
    public static final CategoryTranslation TRANSLATION = new CategoryTranslation();
    public static final CategoryTechStack TECH_STACK = new CategoryTechStack();

    public static final class CategoryCommands {
        public final ModConfigSpec.ConfigValue<String> tpaDimension;

        private CategoryCommands() {
            COMMON_BUILDER.comment("Commands").push("commands");

            tpaDimension = COMMON_BUILDER
                    .comment("If not blank, the /tpa command will only be allowed in this dimension")
                    .define("tpaDimension", "tropicraft:tropics");

            COMMON_BUILDER.pop();
        }
    }

    public static final class CategoryTranslation {
        public final ModConfigSpec.ConfigValue<Boolean> translateOutgoing;
        public final ModConfigSpec.ConfigValue<Boolean> translateIncoming;
        public final ModConfigSpec.ConfigValue<Boolean> prompted;

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
        public final ModConfigSpec.ConfigValue<String> authKey;
        public final ModConfigSpec.ConfigValue<String> scheduleUrl;
        public final ModConfigSpec.ConfigValue<String> translationUrl;

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

    public static final ModConfigSpec COMMON_CONFIG = COMMON_BUILDER.build();
    public static final ModConfigSpec CLIENT_CONFIG = CLIENT_BUILDER.build();

    @SubscribeEvent
    public static void configLoad(ModConfigEvent.Loading event) {
    }

    @SubscribeEvent
    public static void configReload(ModConfigEvent.Reloading event) {
    }
}
