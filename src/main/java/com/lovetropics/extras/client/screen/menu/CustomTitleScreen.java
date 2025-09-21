package com.lovetropics.extras.client.screen.menu;

import com.lovetropics.extras.ExtraLangKeys;
import com.lovetropics.extras.ExtrasConfig;
import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.client.screen.CustomSpritesButton;
import com.lovetropics.extras.mixin.client.AbstractWidgetAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber(modid = LTExtras.MODID, value = Dist.CLIENT)
public class CustomTitleScreen {
    private static final Component SINGLEPLAYER_TEXT = Component.translatable("menu.singleplayer");
    private static final Component MULTIPLAYER_TEXT = Component.translatable("menu.multiplayer");
    private static final Component REALMS_TEXT = Component.translatable("menu.online");
    private static final Component MODS_TEXT = Component.translatable("fml.menu.mods");
    private static final Component OPTIONS_TEXT = Component.translatable("menu.options");
    private static final Component QUIT_TEXT = Component.translatable("menu.quit");
    private static final Component ACCESSIBILITY_TEXT = Component.translatable("options.accessibility");
    private static final Component LANGUAGE_TEXT = Component.translatable("options.language");
    private static final Component CREATE_TEST_WORLD_TEXT = Component.literal("Create Test World");

    private static final WidgetSprites CONNECT_SPRITES = new WidgetSprites(
            LTExtras.location("widget/connect"),
            LTExtras.location("widget/connect_disabled"),
            LTExtras.location("widget/connect_highlighted")
    );

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        Button singleplayer = findButton(event, SINGLEPLAYER_TEXT);
        Button multiplayer = findButton(event, MULTIPLAYER_TEXT);
        Button realms = findButton(event, REALMS_TEXT);
        Button mods = findButton(event, MODS_TEXT);
        Button options = findButton(event, OPTIONS_TEXT);
        Button quit = findButton(event, QUIT_TEXT);
        Button accessibility = findButton(event, ACCESSIBILITY_TEXT);
        Button language = findButton(event, LANGUAGE_TEXT);
        if (singleplayer == null || multiplayer == null || realms == null || mods == null || options == null || quit == null || accessibility == null || language == null) {
            // Screen has been changed in some way we didn't expect - player might have installed another mod that changes it, let that apply
            return;
        }
        event.removeListener(realms);
        event.removeListener(mods);

        Button createTestWorldButton = findButton(event, CREATE_TEST_WORLD_TEXT);
        if (createTestWorldButton != null) {
            event.removeListener(createTestWorldButton);
        }

        CustomSpritesButton connectButton = new CustomSpritesButton(CONNECT_SPRITES, Button.builder(ExtraLangKeys.MENU_CONNECT.get(), button -> connectToEvent(event.getScreen()))
                .pos(singleplayer.getX(), singleplayer.getY())
                .width(singleplayer.getWidth()));
        connectButton.active = multiplayer.active;
        connectButton.setTooltip(((AbstractWidgetAccess) multiplayer).getTooltip().get());
        event.addListener(connectButton);

        event.addListener(Button.builder(ExtraLangKeys.MENU_DONATE.get(), ConfirmLinkScreen.confirmLink(event.getScreen(), ExtrasConfig.DONATE_URL.get(), true))
                .pos(multiplayer.getX(), multiplayer.getY())
                .width(multiplayer.getWidth())
                .build());

        singleplayer.setPosition(options.getX(), mods.getY() + 5);
        singleplayer.setWidth(options.getWidth());

        multiplayer.setPosition(quit.getX(), singleplayer.getY());
        multiplayer.setWidth(quit.getWidth());

        int bottomRowY = singleplayer.getY() + singleplayer.getHeight() + 4;
        options.setY(bottomRowY);
        quit.setY(bottomRowY);

        int sideButtonY = (singleplayer.getY() + bottomRowY) / 2;
        language.setY(sideButtonY);
        accessibility.setY(sideButtonY);
    }

    private static void connectToEvent(Screen parentScreen) {
        ServerAddress address = ServerAddress.parseString(ExtrasConfig.SERVER_ADDRESS.get());
        ServerData serverData = new ServerData("Love Tropics", address.toString(), ServerData.Type.OTHER);
        ConnectScreen.startConnecting(parentScreen, Minecraft.getInstance(), address, serverData, false, null);
    }

    @Nullable
    private static Button findButton(ScreenEvent.Init.Post event, Component label) {
        return (Button) event.getListenersList().stream()
                .filter(widget -> widget instanceof Button button && button.getMessage().equals(label))
                .findFirst()
                .orElse(null);
    }
}
