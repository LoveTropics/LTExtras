package com.lovetropics.extras.client.item;

import com.lovetropics.extras.block.entity.WordBoxBlockEntity;
import com.lovetropics.extras.client.block.WordBoxBlockEntityRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Objects;
import java.util.Set;

public class WordBoxSpecialRenderer implements SpecialModelRenderer<Component> {
    private final Font font;

    public WordBoxSpecialRenderer(Font font) {
        this.font = font;
    }

    @Override
    public void render(@Nullable Component text, ItemDisplayContext context, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, boolean hasFoilType) {
        WordBoxBlockEntityRenderer.renderText(poseStack, bufferSource, font, packedLight, Objects.requireNonNullElse(text, WordBoxBlockEntity.DEFAULT_TEXT));
    }

    @Override
    public void getExtents(Set<Vector3f> vertices) {
        // The actual model will populate these
    }

    @Override
    public @Nullable Component extractArgument(ItemStack itemStack) {
        return itemStack.get(DataComponents.CUSTOM_NAME);
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(Unbaked::new);

        @Override
        public SpecialModelRenderer<?> bake(EntityModelSet modelSet) {
            return new WordBoxSpecialRenderer(Minecraft.getInstance().font);
        }

        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }
    }
}
