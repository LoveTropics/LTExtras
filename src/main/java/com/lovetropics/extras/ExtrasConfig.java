package com.lovetropics.extras;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.lovetropics.extras.duck.ServicesHolder;
import com.lovetropics.extras.techstack.ExtrasTechstackSubscriber;
import com.mojang.authlib.Environment;
import com.mojang.authlib.EnvironmentParser;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.exceptions.MinecraftClientException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.client.MinecraftClient;
import com.mojang.authlib.yggdrasil.ProfileResult;
import com.mojang.authlib.yggdrasil.ServicesKeySet;
import com.mojang.authlib.yggdrasil.YggdrasilEnvironment;
import com.mojang.authlib.yggdrasil.YggdrasilGameProfileRepository;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import com.mojang.authlib.yggdrasil.response.MinecraftProfilePropertiesResponse;
import com.mojang.authlib.yggdrasil.response.ProfileAction;
import com.mojang.logging.LogUtils;
import com.mojang.util.UndashedUuid;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Services;
import net.minecraft.server.players.ProfileResolver;
import net.minecraft.util.StringUtil;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import java.net.Proxy;
import java.net.URL;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@EventBusSubscriber
public class ExtrasConfig {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final ModConfigSpec.Builder COMMON_BUILDER = new ModConfigSpec.Builder();
    private static final ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();

    public static final CategoryCommands COMMANDS = new CategoryCommands();
    public static final CategoryTranslation TRANSLATION = new CategoryTranslation();
    public static final CategoryTechStack TECH_STACK = new CategoryTechStack();

    private static @Nullable Services initialServices;

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
        public final ModConfigSpec.ConfigValue<String> profileServiceUrlOverride;
        public final ModConfigSpec.ConfigValue<String> profileSessionServiceUrlOverride;

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

            profileServiceUrlOverride = COMMON_BUILDER
                    .comment("URL override for Mojang minecraftservices API used for profile lookups (i.e. https://api.minecraftservices.com under /minecraft/profile/lookup/name/<name>)")
                    .define("profileServiceUrlOverride", "");

            profileSessionServiceUrlOverride = COMMON_BUILDER
                    .comment("URL override for Mojang sessionserver API used for profile lookups (i.e. https://sessionserver.mojang.com under /session/minecraft/profile/<uuid>)")
                    .define("profileSessionServiceUrlOverride", "");

            COMMON_BUILDER.pop();
        }
    }

    public static final ModConfigSpec.ConfigValue<Boolean> CLIENT_AUTO_REJOIN = COMMON_BUILDER
            .comment("If true, clients will automatically try to reconnect when the server closes.")
            .define("clientAutoRejoin", false);

    public static final ModConfigSpec.ConfigValue<String> SERVER_ADDRESS = COMMON_BUILDER
            .comment("The IP address for the Love Tropics event server")
            .define("serverAddress", "play.lovetropics.org");

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

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            setupServices(server);
        }
    }

    @SubscribeEvent
    public static void serverStarted(ServerStartedEvent event) {
        setupServices(event.getServer());
    }

    private static void setupServices(MinecraftServer server) {
        if (initialServices == null) {
            initialServices = server.services();
        }

        String profileServiceUrl = TECH_STACK.profileServiceUrlOverride.get();
        String profileSessionServiceUrl = TECH_STACK.profileSessionServiceUrlOverride.get();
        if (StringUtil.isBlank(profileServiceUrl)) {
            profileServiceUrl = null;
        }
        if (StringUtil.isBlank(profileSessionServiceUrl)) {
            profileSessionServiceUrl = null;
        }

        if ((profileServiceUrl == null) != (profileSessionServiceUrl == null)) {
            LOGGER.warn("Both profileServiceUrlOverride and profileSessionServiceUrlOverride must be set");
            return;
        }

        Services newServices;
        if (profileServiceUrl != null) {
            newServices = setupServices(initialServices, server.getProxy(), profileServiceUrl, profileSessionServiceUrl);
        } else {
            newServices = initialServices;
        }
        ((ServicesHolder) server).lt$setServices(newServices);
    }

    private static Services setupServices(Services initialServices, Proxy proxy, String profileServiceUrl, String profileSessionServiceUrl) {
        if (!profileServiceUrl.endsWith("/")) {
            profileServiceUrl = profileServiceUrl + "/";
        }
        if (!profileSessionServiceUrl.endsWith("/")) {
            profileSessionServiceUrl = profileSessionServiceUrl + "/";
        }

        Environment defaultEnv = EnvironmentParser.getEnvironmentFromProperties().orElse(YggdrasilEnvironment.PROD.getEnvironment());

        MinecraftSessionService sessionService = createSessionService(profileSessionServiceUrl, proxy, defaultEnv, initialServices.servicesKeySet());

        return new Services(
                sessionService,
                initialServices.servicesKeySet(),
                createProfileRepository(proxy, profileServiceUrl),
                initialServices.nameToIdCache(),
                new ProfileResolver.Cached(sessionService, initialServices.nameToIdCache())
        );
    }

    private static MinecraftSessionService createSessionService(String serviceUrl, Proxy proxy, Environment defaultEnv, ServicesKeySet servicesKeySet) {
        // We don't want to redirect hasjoined endpoint, so we need to duplicate this
        return new YggdrasilMinecraftSessionService(servicesKeySet, proxy, defaultEnv) {
            private final LoadingCache<UUID, Optional<ProfileResult>> insecureProfiles = CacheBuilder.newBuilder()
                    .expireAfterWrite(Duration.ofHours(6))
                    .build(new CacheLoader<>() {
                        @Override
                        public Optional<ProfileResult> load(UUID key) {
                            return Optional.ofNullable(fetchProfileUncached(key, false));
                        }
                    });

            private final MinecraftClient client = MinecraftClient.unauthenticated(proxy);

            @Override
            public @Nullable ProfileResult fetchProfile(UUID profileId, boolean requireSecure) {
                if (!requireSecure) {
                    return insecureProfiles.getUnchecked(profileId).orElse(null);
                }
                return fetchProfileUncached(profileId, true);
            }

            private @Nullable ProfileResult fetchProfileUncached(UUID profileId, boolean requireSecure) {
                try {
                    URL url = HttpAuthenticationService.constantURL(serviceUrl + "/session/minecraft/profile/" + UndashedUuid.toString(profileId));
                    url = HttpAuthenticationService.concatenateURL(url, "unsigned=" + !requireSecure);
                    MinecraftProfilePropertiesResponse response = client.get(url, MinecraftProfilePropertiesResponse.class);
                    if (response == null) {
                        LOGGER.debug("Couldn't fetch profile properties for {} as the profile does not exist", profileId);
                        return null;
                    }
                    LOGGER.debug("Successfully fetched profile properties for {}", response.profile());
                    return new ProfileResult(response.profile(), response.profileActions().stream().map(ProfileAction::type).collect(Collectors.toSet()));
                } catch (MinecraftClientException | IllegalArgumentException e) {
                    LOGGER.warn("Couldn't look up profile properties for {}", profileId, e);
                    return null;
                }
            }
        };
    }

    private static GameProfileRepository createProfileRepository(Proxy proxy, String serviceUrl) {
        Environment dummyEnv = new Environment("", "", serviceUrl, "");
        return new YggdrasilGameProfileRepository(proxy, dummyEnv);
    }
}
