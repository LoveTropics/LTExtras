package com.lovetropics.extras;

import com.lovetropics.extras.techstack.ExtrasTechstackSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = LTExtras.MODID)
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
        public final ModConfigSpec.ConfigValue<Boolean> prompted;

        private CategoryTranslation() {
            CLIENT_BUILDER.comment("Translation").push("translation");

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
        public final ModConfigSpec.ConfigValue<String> webSocketUrl;

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

            webSocketUrl = COMMON_BUILDER
                    .comment("URL the web socket is running on")
                    .define("webSocketUrl", "wss://localhost:443/ws");

            COMMON_BUILDER.pop();
        }
    }

    public static final ModConfigSpec.ConfigValue<Boolean> CLIENT_AUTO_REJOIN = COMMON_BUILDER
            .comment("If true, clients will automatically try to reconnect when the server closes.")
            .define("clientAutoRejoin", false);

    public static final ModConfigSpec.ConfigValue<String> SERVER_ADDRESS = COMMON_BUILDER
            .comment("The IP address for the Love Tropics event server")
            .define("serverAddress", "volcano.lovetropics.org");

    public static final ModConfigSpec.ConfigValue<String> DONATE_URL = COMMON_BUILDER
            .comment("The URL that players should be directed to for donating.")
            .define("donateUrl", "https://lovetropics.org/donate");

    public static final ModConfigSpec COMMON_CONFIG = COMMON_BUILDER.build();
    public static final ModConfigSpec CLIENT_CONFIG = CLIENT_BUILDER.build();

    @SubscribeEvent
    public static void configLoad(ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() == COMMON_CONFIG) {
            onCommonConfigLoad();
        }
    }

    @SubscribeEvent
    public static void configReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == COMMON_CONFIG) {
            onCommonConfigLoad();
        }
    }

    private static void onCommonConfigLoad() {
        ExtrasTechstackSubscriber.updateConfig(TECH_STACK.webSocketUrl.get(), TECH_STACK.authKey.get());
    }
}
