package com.lovetropics.extras.mixin.client.gui;

import com.lovetropics.extras.ExtraDataComponents;
import com.lovetropics.extras.client.screen.container.LootInventoryScreenUnderlay;
import com.lovetropics.extras.template.item.modifers.STTLootCategoryDisplayData;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * LTExtras
 * FIRE EMOJI, FIRE EMOJI, FIRE EMOJI
 */
@Mixin(Hud.class)
public class HudMixin {

    @Inject(method = "extractSlot", at = @At("HEAD"))
    private void extractSlot(GuiGraphicsExtractor graphics, int x, int y, DeltaTracker deltaTracker, Player player, ItemStack itemStack, int seed, CallbackInfo ci) {
        if (!itemStack.isEmpty() && itemStack.get(ExtraDataComponents.STT_LOOT_CATEGORY_DISPLAY) instanceof STTLootCategoryDisplayData displayData) {
            LootInventoryScreenUnderlay.injectHotbarUnderlay(graphics, displayData, x, y);
        }
    }
}
