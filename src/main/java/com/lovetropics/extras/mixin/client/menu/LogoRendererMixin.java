package com.lovetropics.extras.mixin.client.menu;

import com.lovetropics.extras.LTExtras;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.LogoRenderer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LogoRenderer.class)
public class LogoRendererMixin {
    @Shadow
    @Final
    private boolean keepLogoThroughFade;
    @Unique
    private static final Identifier LOVE_TROPICS_TEXTURE = LTExtras.location("textures/gui/title.png");

    @ModifyConstant(method = "extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IF)V", constant = @Constant(intValue = 30))
    private int modifyDefaultHeight(int height) {
        return height - 10;
    }

    /**
     * @author Gegy
     * @reason Replace the game logo
     */
    @Overwrite
    public void extractRenderState(GuiGraphicsExtractor graphics, int screenWidth, float transparency, int height) {
        int x = (screenWidth - 256) / 2;
        int color = ARGB.white(keepLogoThroughFade ? 1.0f : transparency);
        graphics.blit(RenderPipelines.GUI_TEXTURED, LOVE_TROPICS_TEXTURE, x, height, 0.0f, 0.0f, 256, 38, 256, 64, color);
    }
}
