package com.lovetropics.extras.translation;

import com.lovetropics.extras.ExtraLangKeys;
import com.lovetropics.extras.ExtrasConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CommonButtons;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.LanguageSelectScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class TranslationPromptScreen extends Screen {
    private static final Component TITLE = ExtraLangKeys.TRANSLATION_PROMPT_TITLE.get();
    private static final Component MESSAGE = ExtraLangKeys.TRANSLATION_PROMPT.get();

    private static final int MAX_WIDTH = 300;

    private final Runnable callback;
    private final LinearLayout layout = LinearLayout.vertical().spacing(8);

    public TranslationPromptScreen(Runnable callback, Minecraft minecraft, Font font) {
        super(TITLE);
        this.callback = callback;

        layout.defaultCellSetting().alignHorizontallyCenter();

        layout.addChild(new StringWidget(TITLE, font), layout.newCellSettings().paddingVertical(10));

        layout.addChild(new MultiLineTextWidget(MESSAGE, font).setCentered(true).setMaxWidth(MAX_WIDTH));

        layout.addChild(CommonButtons.language(Button.DEFAULT_WIDTH, button -> minecraft.setScreenAndShow(new LanguageSelectScreen(this, minecraft.options, minecraft.getLanguageManager())), false));

        layout.addChild(Button.builder(CommonComponents.GUI_DONE, b -> onClose()).build(), layout.newCellSettings().paddingVertical(10));
    }

    @Override
    protected void init() {
        layout.visitWidgets(this::addRenderableWidget);
        repositionElements();
    }

    @Override
    protected void repositionElements() {
        layout.arrangeElements();
        FrameLayout.centerInRectangle(layout, getRectangle());
    }

    @Override
    public Component getNarrationMessage() {
        return CommonComponents.joinForNarration(TITLE, MESSAGE);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public void onClose() {
        ExtrasConfig.TRANSLATION.prompted.set(true);
        ExtrasConfig.CLIENT_CONFIG.save();
        callback.run();
    }
}
