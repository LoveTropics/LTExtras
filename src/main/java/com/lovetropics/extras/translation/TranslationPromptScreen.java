package com.lovetropics.extras.translation;

import com.lovetropics.extras.ExtraLangKeys;
import com.lovetropics.extras.ExtrasConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.LanguageSelectScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class TranslationPromptScreen extends Screen {
    private static final Component TITLE = ExtraLangKeys.TRANSLATION_PROMPT_TITLE.get();
    private static final Component MESSAGE = ExtraLangKeys.TRANSLATION_PROMPT.get();

    private static final int MAX_WIDTH = 300;
    private static final int BUTTON_WIDTH = 200;

    private final Runnable callback;
    private final GridLayout layout = new GridLayout().spacing(8);

    private final MultiLineTextWidget warning;
    private final CycleButton<Boolean> translateIncoming;
    private final CycleButton<Boolean> translateOutgoing;

    public TranslationPromptScreen(final Runnable callback, final Minecraft minecraft, final Font font) {
        super(TITLE);
        this.callback = callback;

        layout.defaultCellSetting().alignHorizontallyCenter();

        final GridLayout.RowHelper helper = layout.createRowHelper(1);
        helper.addChild(new StringWidget(TITLE, font).alignCenter(), layout.newCellSettings().paddingVertical(10));

        helper.addChild(new MultiLineTextWidget(MESSAGE, font).setCentered(true).setMaxWidth(MAX_WIDTH));

        final TextAndImageButton languageButton = TextAndImageButton.builder(
                Component.translatable("options.language"), Button.WIDGETS_LOCATION,
                button -> minecraft.setScreen(new LanguageSelectScreen(this, minecraft.options, minecraft.getLanguageManager()))
        ).texStart(3, 109).offset(90, 3).yDiffTex(20).usedTextureSize(14, 14).textureSize(256, 256).build();

        languageButton.setWidth(BUTTON_WIDTH);
        helper.addChild(languageButton);

        warning = helper.addChild(new MultiLineTextWidget(ExtraLangKeys.TRANSLATION_PROMPT_UNRECOGNIZED.get().withStyle(ChatFormatting.GOLD), font).setMaxWidth(MAX_WIDTH).setCentered(true));

        translateIncoming = helper.addChild(CycleButton.booleanBuilder(CommonComponents.GUI_YES, CommonComponents.GUI_NO)
                .withInitialValue(ExtrasConfig.TRANSLATION.translateIncoming.get())
                .create(0, 0, BUTTON_WIDTH, Button.DEFAULT_HEIGHT, ExtraLangKeys.TRANSLATION_PROMPT_INCOMING.get()));
        translateOutgoing = helper.addChild(CycleButton.booleanBuilder(CommonComponents.GUI_YES, CommonComponents.GUI_NO)
                .withInitialValue(ExtrasConfig.TRANSLATION.translateOutgoing.get())
                .create(0, 0, BUTTON_WIDTH, Button.DEFAULT_HEIGHT, ExtraLangKeys.TRANSLATION_PROMPT_OUTGOING.get()));

        helper.addChild(Button.builder(CommonComponents.GUI_DONE, b -> onClose()).build(), layout.newCellSettings().paddingVertical(10));
    }

    @Override
    protected void init() {
        layout.visitWidgets(this::addRenderableWidget);
        repositionElements();
    }

    @Override
    protected void repositionElements() {
        updateWidgetStates();
        layout.arrangeElements();
        FrameLayout.centerInRectangle(layout, getRectangle());
    }

    private void updateWidgetStates() {
        final String selectedLanguage = minecraft.getLanguageManager().getSelected();
        final boolean translatable = TranslatableLanguage.byKey(selectedLanguage) != null;
        warning.visible = !translatable;
        translateIncoming.active = translatable;
        translateOutgoing.active = translatable;
    }

    @Override
    public void render(final GuiGraphics graphics, final int mouseX, final int mouseY, final float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
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
        final ExtrasConfig.CategoryTranslation config = ExtrasConfig.TRANSLATION;
        config.translateIncoming.set(translateIncoming.getValue());
        config.translateOutgoing.set(translateOutgoing.getValue());
        ExtrasConfig.CLIENT_CONFIG.save();
        callback.run();
    }
}
