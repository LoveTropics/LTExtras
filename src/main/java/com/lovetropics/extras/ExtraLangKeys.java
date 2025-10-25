package com.lovetropics.extras;

import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.providers.ProviderType;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public enum ExtraLangKeys {
    COLLECTIBLE_COMPASS_ALREADY_USED("item", "collectible_compass.already_used", "This Compass already points to a Collectible. If you want to find something else, you will need to find a new one!"),
    COLLECTIBLE_COMPASS_SUCCESS("item", "collectible_compass.success", "\uD83D\uDC40 The Compass has found a Collectible that you don't have yet, and now points towards it."),
    COLLECTIBLE_COMPASS_FAIL("item", "collectible_compass.fail", "The Compass was not able to find any Collectibles that you don't have nearby. Maybe try somewhere else?"),
    COLLECTIBLES_LOCKED("item", "collectible_basket.locked", "Your Collectible Basket has been locked here!"),

    CLUB_INVITE_1_TOP("invite", "club_1.top", "We need you.\nWear a disguise.\nThey are watching."),
    CLUB_INVITE_1_BOTTOM("invite", "club_1.bottom", "Did you know disguises are fireproof?"),
    CLUB_INVITE_2_TOP("invite", "club_2.top", "See you there.\nWear a disguise.\nGrab a drink."),
    CLUB_INVITE_2_BOTTOM("invite", "club_2.bottom", "I hear the Limeade's good."),

    TRANSLATION_PROMPT_TITLE("screen", "translation_prompt.title", "Chat Translation"),
    TRANSLATION_PROMPT("screen", "translation_prompt.message", "Love Tropics is multi-lingual! We will try our best to translate chat for you - but to do this, we need to know what language you will be speaking and would like to see.\n\nIn-game content is translated for English, Spanish, French, and German."),

    SERVER_CLOSED_TITLE("screen", "server_closed.title", "Disconnected from server"),
    SERVER_CLOSED_UNEXPECTED("screen", "server_closed.unexpected", "The server unexpectedly shut down, or you lost connection:\n%s"),
    SERVER_CLOSED_RESTART("screen", "server_closed.restart", "The server has shut down for a quick restart - it should be back in a few moments!"),
    SERVER_CLOSED_CLIENT_RESTART("screen", "server_closed.client_restart", "We are making some upgrades to our systems: to rejoin, please restart your game to get the needed updates.\n\nApologies for the inconvenience!"),
    SERVER_CLOSED_PERMANENT("screen", "server_closed.permanent", "Love Tropics '25 has unfortunately now come to a close.\nThank you so much for your time and donation - we hope to see you again next year!"),
    SERVER_CLOSED_AUTO_JOIN("screen", "server_closed.auto_join", "As soon as the server is back online, you will be joined back automatically."),

    MENU_CONNECT("screen", "menu_connect", "Connect to Love Tropics"),
    MENU_DONATE("screen", "menu_donate", "Donate to The Pachamama Alliance"),

    FORKLIFT_CERTIFICATION_MISSING("forklift", "certification.missing", "You must have a Forklift Certification to drive this forklift"),
    ;

    private final String key;
    private final String value;

    ExtraLangKeys(String type, String key, String value) {
        this.key = Util.makeDescriptionId(type, LTExtras.location(key));
        this.value = value;
    }

    public MutableComponent get() {
        return Component.translatable(key);
    }

    public MutableComponent format(Object... args) {
        return Component.translatable(key, args);
    }

    public static void init(Registrate registrate) {
        registrate.addDataGenerator(ProviderType.LANG, prov -> {
            for (ExtraLangKeys lang : values()) {
                prov.add(lang.key, lang.value);
            }
        });
    }
}
