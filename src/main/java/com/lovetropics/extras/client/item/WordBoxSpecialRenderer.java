package com.lovetropics.extras.client.item;

import com.lovetropics.extras.block.entity.WordBoxBlockEntity;
import com.lovetropics.extras.client.block.WordBoxBlockEntityRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

public class WordBoxSpecialRenderer implements SpecialModelRenderer<Component> {
    private final Font font;

    public WordBoxSpecialRenderer(Font font) {
        this.font = font;
    }

    @Override
    public void submit(@org.jspecify.annotations.Nullable Component text, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
        WordBoxBlockEntityRenderer.submitText(poseStack, submitNodeCollector, font, lightCoords, Objects.requireNonNullElse(text, WordBoxBlockEntity.DEFAULT_TEXT));
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        // The actual model will populate these
    }

    @Override
    public @Nullable Component extractArgument(ItemStack itemStack) {
        return itemStack.get(DataComponents.CUSTOM_NAME);
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked<Component> {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(Unbaked::new);

        @Override
        public SpecialModelRenderer<Component> bake(BakingContext context) {
            return new WordBoxSpecialRenderer(Minecraft.getInstance().font);
        }

        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }
    }
}
