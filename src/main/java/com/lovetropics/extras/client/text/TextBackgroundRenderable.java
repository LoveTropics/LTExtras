package com.lovetropics.extras.client.text;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.TextRenderable;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.gui.GuiMetadataSection;
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;
import net.minecraft.util.Mth;
import org.joml.Matrix4fc;

public record TextBackgroundRenderable(
        Identifier sprite,
        float left, float top, float right, float bottom, float z,
        int color
) implements TextRenderable {
    @Override
    public void render(Matrix4fc pose, VertexConsumer buffer, int packedLightCoords, boolean flat) {
        TextureAtlas atlas = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.GUI);
        TextureAtlasSprite sprite = atlas.getSprite(this.sprite);
        GuiSpriteScaling scaling = getSpriteScaling(sprite);
        switch (scaling) {
            case GuiSpriteScaling.Tile tile ->
                    addTiledQuad(buffer, pose, packedLightCoords, sprite, left, top, right, bottom, 0, 0, tile.width(), tile.height(), tile.width(), tile.height());
            case GuiSpriteScaling.NineSlice nineSlice ->
                    addNineSliced(buffer, pose, packedLightCoords, sprite, nineSlice);
            default ->
                    addQuad(buffer, pose, packedLightCoords, left, top, right, bottom, sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1());
        }
    }

    /// wow, look at that high-effort copy pasta (from [net.minecraft.client.gui.GuiGraphicsExtractor])
    private void addNineSliced(
            VertexConsumer builder, Matrix4fc pose, int packedLight,
            TextureAtlasSprite sprite, GuiSpriteScaling.NineSlice nineSlice
    ) {
        GuiSpriteScaling.NineSlice.Border border = nineSlice.border();
        float width = right - left;
        float height = bottom - top;
        float borderLeft = Math.min(border.left(), width / 2.0f);
        float borderRight = Math.min(border.right(), width / 2.0f);
        float borderTop = Math.min(border.top(), height / 2.0f);
        float borderBottom = Math.min(border.bottom(), height / 2.0f);
        if (width == nineSlice.width() && height == nineSlice.height()) {
            addQuad(builder, pose, packedLight, sprite, nineSlice.width(), nineSlice.height(), 0, 0, left, top, right, bottom);
            return;
        } else if (height == nineSlice.height()) {
            addQuad(builder, pose, packedLight, sprite, nineSlice.width(), nineSlice.height(), 0, 0, left, top, left + borderLeft, bottom);
            addNineSlicedInnerQuad(
                    builder, pose, packedLight,
                    nineSlice,
                    sprite,
                    left + borderLeft,
                    top,
                    right - borderRight,
                    bottom,
                    borderLeft,
                    0,
                    nineSlice.width() - borderRight - borderLeft,
                    nineSlice.height(),
                    nineSlice.width(),
                    nineSlice.height()
            );
            addQuad(
                    builder, pose, packedLight,
                    sprite,
                    nineSlice.width(),
                    nineSlice.height(),
                    nineSlice.width() - borderRight,
                    0,
                    right - borderRight,
                    top,
                    right,
                    bottom
            );
            return;
        } else if (width == nineSlice.width()) {
            addQuad(builder, pose, packedLight, sprite, nineSlice.width(), nineSlice.height(), 0, 0, left, top, right, top + borderTop);
            addNineSlicedInnerQuad(
                    builder, pose, packedLight,
                    nineSlice,
                    sprite,
                    left,
                    top + borderTop,
                    right,
                    bottom - borderBottom,
                    0,
                    borderTop,
                    nineSlice.width(),
                    nineSlice.height() - borderBottom - borderTop,
                    nineSlice.width(),
                    nineSlice.height()
            );
            addQuad(
                    builder, pose, packedLight,
                    sprite,
                    nineSlice.width(),
                    nineSlice.height(),
                    0,
                    nineSlice.height() - borderBottom,
                    left,
                    bottom - borderBottom,
                    right,
                    bottom
            );
            return;
        }

        // Top-left corner
        addQuad(
                builder, pose, packedLight,
                sprite,
                nineSlice.width(),
                nineSlice.height(),
                0,
                0,
                left,
                top,
                left + borderLeft,
                top + borderTop
        );
        // Top edge
        addNineSlicedInnerQuad(
                builder, pose, packedLight,
                nineSlice,
                sprite,
                left + borderLeft,
                top,
                right - borderRight,
                top + borderTop,
                borderLeft,
                0,
                nineSlice.width() - borderRight - borderLeft,
                borderTop,
                nineSlice.width(),
                nineSlice.height()
        );
        // Top-right corner
        addQuad(
                builder, pose, packedLight,
                sprite,
                nineSlice.width(),
                nineSlice.height(),
                nineSlice.width() - borderRight,
                0,
                right - borderRight,
                top,
                right,
                top + borderTop
        );
        // Bottom-left corner
        addQuad(
                builder, pose, packedLight,
                sprite,
                nineSlice.width(),
                nineSlice.height(),
                0,
                nineSlice.height() - borderBottom,
                left,
                bottom - borderBottom,
                left + borderLeft,
                bottom
        );
        // Bottom edge
        addNineSlicedInnerQuad(
                builder, pose, packedLight,
                nineSlice,
                sprite,
                left + borderLeft,
                bottom - borderBottom,
                right - borderRight,
                bottom,
                borderLeft,
                nineSlice.height() - borderBottom,
                nineSlice.width() - borderRight - borderLeft,
                borderBottom,
                nineSlice.width(),
                nineSlice.height()
        );
        // Bottom-right corner
        addQuad(
                builder, pose, packedLight,
                sprite,
                nineSlice.width(),
                nineSlice.height(),
                nineSlice.width() - borderRight,
                nineSlice.height() - borderBottom,
                right - borderRight,
                bottom - borderBottom,
                right,
                bottom
        );
        // Left edge
        addNineSlicedInnerQuad(
                builder, pose, packedLight,
                nineSlice,
                sprite,
                left,
                top + borderTop,
                left + borderLeft,
                bottom - borderBottom,
                0,
                borderTop,
                borderLeft,
                nineSlice.height() - borderBottom - borderTop,
                nineSlice.width(),
                nineSlice.height()
        );
        // Center
        addNineSlicedInnerQuad(
                builder, pose, packedLight,
                nineSlice,
                sprite,
                left + borderLeft,
                top + borderTop,
                right - borderRight,
                bottom - borderBottom,
                borderLeft,
                borderTop,
                nineSlice.width() - borderRight - borderLeft,
                nineSlice.height() - borderBottom - borderTop,
                nineSlice.width(),
                nineSlice.height()
        );
        // Right edge
        addNineSlicedInnerQuad(
                builder, pose, packedLight,
                nineSlice,
                sprite,
                right - borderRight,
                top + borderTop,
                right,
                bottom - borderBottom,
                nineSlice.width() - borderRight,
                borderTop,
                borderRight,
                nineSlice.height() - borderBottom - borderTop,
                nineSlice.width(),
                nineSlice.height()
        );
    }

    private void addNineSlicedInnerQuad(
            VertexConsumer builder, Matrix4fc pose, int packedLight,
            GuiSpriteScaling.NineSlice nineSlice, TextureAtlasSprite sprite,
            float left, float top,
            float right, float bottom,
            float textureX, float textureY,
            float textureWidth, float textureHeight,
            int spriteWidth, int spriteHeight
    ) {
        if (right <= left || bottom <= top) {
            return;
        }
        if (nineSlice.stretchInner()) {
            addQuad(
                    builder,
                    pose,
                    packedLight,
                    left, top,
                    right, bottom,
                    sprite.getU(textureX / spriteWidth),
                    sprite.getU((textureX + textureWidth) / spriteWidth),
                    sprite.getV(textureY / spriteHeight),
                    sprite.getV((textureY + textureHeight) / spriteHeight)
            );
        } else {
            addTiledQuad(builder, pose, packedLight, sprite, left, top, right, bottom, textureX, textureY, textureWidth, textureHeight, spriteWidth, spriteHeight);
        }
    }

    private void addQuad(
            VertexConsumer builder, Matrix4fc pose, int packedLight,
            TextureAtlasSprite sprite,
            int spriteWidth, int spriteHeight,
            float textureX, float textureY,
            float left, float top, float right, float bottom
    ) {
        addQuad(
                builder, pose, packedLight,
                left, top, right, bottom,
                sprite.getU(textureX / spriteWidth),
                sprite.getU((textureX + right - left) / spriteWidth),
                sprite.getV(textureY / spriteHeight),
                sprite.getV((textureY + bottom - top) / spriteHeight)
        );
    }

    private void addQuad(VertexConsumer builder, Matrix4fc pose, int packedLight, float left, float top, float right, float bottom, float u0, float u1, float v0, float v1) {
        if (right <= left || bottom <= top) {
            return;
        }
        builder.addVertex(pose, left, top, z).setUv(u0, v0).setColor(color).setLight(packedLight);
        builder.addVertex(pose, left, bottom, z).setUv(u0, v1).setColor(color).setLight(packedLight);
        builder.addVertex(pose, right, bottom, z).setUv(u1, v1).setColor(color).setLight(packedLight);
        builder.addVertex(pose, right, top, z).setUv(u1, v0).setColor(color).setLight(packedLight);
    }

    private void addTiledQuad(
            VertexConsumer builder, Matrix4fc pose, int packedLight,
            TextureAtlasSprite sprite,
            float left, float top,
            float right, float bottom,
            float textureX, float textureY,
            float tileWidth, float tileHeight,
            int spriteWidth, int spriteHeight
    ) {
        if (right <= left || bottom <= top) {
            return;
        }
        if (tileWidth <= 0 || tileHeight <= 0) {
            throw new IllegalArgumentException("Tile size must be positive, got " + tileWidth + "x" + tileHeight);
        }
        addTiledQuad(
                builder, pose, packedLight,
                tileWidth,
                tileHeight,
                left, top,
                right, bottom,
                sprite.getU(textureX / spriteWidth),
                sprite.getU((textureX + tileWidth) / spriteWidth),
                sprite.getV(textureY / spriteHeight),
                sprite.getV((textureY + tileHeight) / spriteHeight)
        );
    }

    private void addTiledQuad(VertexConsumer builder, Matrix4fc pose, int packedLight, float tileWidth, float tileHeight, float left, float top, float right, float bottom, float u0, float u1, float v0, float v1) {
        float width = right - left;
        float height = bottom - top;

        for (float tileX = 0; tileX < width; tileX += tileWidth) {
            float remainingWidth = width - tileX;
            float _tileWidth;
            float _u1;
            if (tileWidth <= remainingWidth) {
                _tileWidth = tileWidth;
                _u1 = u1;
            } else {
                _tileWidth = remainingWidth;
                _u1 = Mth.lerp(remainingWidth / tileWidth, u0, u1);
            }

            for (float tileY = 0; tileY < height; tileY += tileHeight) {
                float remainingHeight = height - tileY;
                float _tileHeight;
                float _v1;
                if (tileHeight <= remainingHeight) {
                    _tileHeight = tileHeight;
                    _v1 = v1;
                } else {
                    _tileHeight = remainingHeight;
                    _v1 = Mth.lerp(remainingHeight / tileHeight, v0, v1);
                }
                addQuad(builder, pose, packedLight, left + tileX, top + tileY, left + tileX + _tileWidth, top + tileY + _tileHeight, u0, _u1, v0, _v1);
            }
        }
    }

    private static GuiSpriteScaling getSpriteScaling(TextureAtlasSprite sprite) {
        return sprite.contents().getAdditionalMetadata(GuiMetadataSection.TYPE).orElse(GuiMetadataSection.DEFAULT).scaling();
    }

    @Override
    public RenderType renderType(Font.DisplayMode displayMode) {
        return switch (displayMode) {
            case NORMAL -> RenderTypes.text(Sheets.GUI_SHEET);
            case SEE_THROUGH -> RenderTypes.textSeeThrough(Sheets.GUI_SHEET);
            case POLYGON_OFFSET -> RenderTypes.textPolygonOffset(Sheets.GUI_SHEET);
        };
    }

    @Override
    public GpuTextureView textureView() {
        return Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.GUI).getTextureView();
    }

    @Override
    public RenderPipeline guiPipeline() {
        return RenderPipelines.GUI_TEXTURED;
    }
}
