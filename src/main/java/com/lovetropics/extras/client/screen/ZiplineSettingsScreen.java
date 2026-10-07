package com.lovetropics.extras.client.screen;

import com.lovetropics.extras.ExtraLangKeys;
import com.lovetropics.extras.network.message.ServerboundZiplineSettingsPacket;
import com.lovetropics.extras.zipline.ZiplineSegment;
import com.lovetropics.extras.zipline.ZiplineSettings;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;

/**
 * Opened by using the wrench on a rope, to configure the rope between two poles.
 */
public class ZiplineSettingsScreen extends Screen {
    private static final Component TITLE = ExtraLangKeys.ZIPLINE_SETTINGS_TITLE.get();
    private static final int WIDGET_WIDTH = 210;
    private static final double SPEED_STEP = 0.5;
    private static final DecimalFormat SPEED_FORMAT = Util.make(new DecimalFormat("0.#"), format -> format.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT)));

    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    private final BlockPos from;
    private final BlockPos to;

    private int slack;
    private float minUpwardSpeed;
    private boolean canLetGo;

    public ZiplineSettingsScreen(ZiplineSegment segment) {
        super(TITLE);
        from = segment.from();
        to = segment.to();
        ZiplineSettings settings = segment.settings();
        slack = settings.slack();
        minUpwardSpeed = settings.minUpwardSpeed();
        canLetGo = settings.canLetGo();
    }

    @Override
    protected void init() {
        layout.addTitleHeader(TITLE, font);

        LinearLayout contents = layout.addToContents(LinearLayout.vertical().spacing(8));
        contents.addChild(new SteppedSlider(
                0, ZiplineSettings.MAX_SLACK, 1, slack,
                value -> ExtraLangKeys.ZIPLINE_SLACK.format((int) value),
                value -> slack = (int) value
        )).setTooltip(Tooltip.create(ExtraLangKeys.ZIPLINE_SLACK_TOOLTIP.get()));
        contents.addChild(new SteppedSlider(
                ZiplineSettings.LOWEST_MIN_UPWARD_SPEED, ZiplineSettings.HIGHEST_MIN_UPWARD_SPEED, SPEED_STEP, minUpwardSpeed,
                value -> ExtraLangKeys.ZIPLINE_MIN_UPWARD_SPEED.format(SPEED_FORMAT.format(value)),
                value -> minUpwardSpeed = (float) value
        )).setTooltip(Tooltip.create(ExtraLangKeys.ZIPLINE_MIN_UPWARD_SPEED_TOOLTIP.get()));
        contents.addChild(CycleButton.onOffBuilder(canLetGo)
                .create(0, 0, WIDGET_WIDTH, Button.DEFAULT_HEIGHT, ExtraLangKeys.ZIPLINE_CAN_LET_GO.get(), (button, value) -> canLetGo = value)
        ).setTooltip(Tooltip.create(ExtraLangKeys.ZIPLINE_CAN_LET_GO_TOOLTIP.get()));

        LinearLayout footer = layout.addToFooter(LinearLayout.horizontal().spacing(8));
        footer.addChild(Button.builder(CommonComponents.GUI_DONE, button -> onDone()).build());
        footer.addChild(Button.builder(CommonComponents.GUI_CANCEL, button -> onClose()).build());

        layout.visitWidgets(this::addRenderableWidget);
        repositionElements();
    }

    @Override
    protected void repositionElements() {
        layout.arrangeElements();
    }

    private void onDone() {
        ClientPacketDistributor.sendToServer(new ServerboundZiplineSettingsPacket(from, to, new ZiplineSettings(slack, minUpwardSpeed, canLetGo)));
        onClose();
    }

    private static class SteppedSlider extends AbstractSliderButton {
        private final double min;
        private final double max;
        private final double step;
        private final DoubleFunction<Component> message;
        private final DoubleConsumer onChange;

        private SteppedSlider(double min, double max, double step, double initialValue, DoubleFunction<Component> message, DoubleConsumer onChange) {
            super(0, 0, WIDGET_WIDTH, Button.DEFAULT_HEIGHT, CommonComponents.EMPTY, Mth.clamp((initialValue - min) / (max - min), 0.0, 1.0));
            this.min = min;
            this.max = max;
            this.step = step;
            this.message = message;
            this.onChange = onChange;
            updateMessage();
        }

        private double getSteppedValue() {
            return Math.round(Mth.lerp(value, min, max) / step) * step;
        }

        @Override
        protected void updateMessage() {
            setMessage(message.apply(getSteppedValue()));
        }

        @Override
        protected void applyValue() {
            onChange.accept(getSteppedValue());
        }
    }
}
