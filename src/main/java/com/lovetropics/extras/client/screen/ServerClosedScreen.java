package com.lovetropics.extras.client.screen;

import com.lovetropics.extras.ExtraLangKeys;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.FocusableTextWidget;
import net.minecraft.client.gui.components.LoadingDotsWidget;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import javax.annotation.Nullable;
import java.util.function.Function;

public class ServerClosedScreen extends Screen {
    private static final Component TITLE = ExtraLangKeys.SERVER_CLOSED_TITLE.get();
    private static final Component QUIT = Component.translatable("menu.quit");

    private final Type type;
    private final ServerData server;
    private final Component description;

    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    @Nullable
    private final AutoJoinServerPinger pinger;

    public ServerClosedScreen(Type type, DisconnectionDetails details, ServerData server) {
        super(TITLE);
        this.type = type;
        this.server = server;
        pinger = type.autoJoin ? new AutoJoinServerPinger(server.ip) : null;

        MutableComponent description = Component.empty();
        description.append(type.description.apply(details));
        if (type.autoJoin) {
            description.append("\n\n").append(ExtraLangKeys.SERVER_CLOSED_AUTO_JOIN.get().withStyle(ChatFormatting.YELLOW));
        }
        this.description = description;
    }

    @Override
    protected void init() {
        layout.addTitleHeader(TITLE, font);

        LinearLayout contents = layout.addToContents(LinearLayout.vertical()).spacing(10);
        contents.defaultCellSetting().alignHorizontallyCenter().alignVerticallyMiddle();

        int textWidth = Window.BASE_WIDTH - 20;
        contents.addChild(new FocusableTextWidget(textWidth, description, font));

        if (type.autoJoin) {
            contents.addChild(new LoadingDotsWidget(font, CommonComponents.EMPTY));
        }

        contents.addChild(SpacerElement.height(Button.DEFAULT_HEIGHT));
        if (type == Type.CLIENT_RESTART) {
            contents.addChild(Button.builder(QUIT, b -> minecraft.stop()).build());
        } else {
            contents.addChild(Button.builder(CommonComponents.GUI_TO_TITLE, b -> onClose()).build());
        }

        layout.visitWidgets(this::addRenderableWidget);
        repositionElements();
    }

    @Override
    protected void repositionElements() {
        layout.arrangeElements();
    }

    @Override
    public void tick() {
        if (pinger != null && pinger.tick()) {
            ServerAddress address = ServerAddress.parseString(server.ip);
            ConnectScreen.startConnecting(new TitleScreen(), minecraft, address, server, false, null);
        }
    }

    @Override
    public Component getNarrationMessage() {
        return CommonComponents.joinForNarration(super.getNarrationMessage(), description);
    }

    @Override
    public void onClose() {
        minecraft.setScreen(new TitleScreen());
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    public enum Type {
        UNEXPECTED(true, details -> ExtraLangKeys.SERVER_CLOSED_UNEXPECTED.format(details.reason().copy().withStyle(ChatFormatting.GRAY))),
        SERVER_RESTART(true, details -> ExtraLangKeys.SERVER_CLOSED_RESTART.get()),
        CLIENT_RESTART(false, details -> ExtraLangKeys.SERVER_CLOSED_CLIENT_RESTART.get()),
        PERMANENT(false, details -> ExtraLangKeys.SERVER_CLOSED_PERMANENT.get());

        private final boolean autoJoin;
        private final Function<DisconnectionDetails, Component> description;

        Type(boolean autoJoin, Function<DisconnectionDetails, Component> description) {
            this.autoJoin = autoJoin;
            this.description = description;
        }
    }
}
