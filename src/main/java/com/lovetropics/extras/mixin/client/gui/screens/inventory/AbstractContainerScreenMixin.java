package com.lovetropics.extras.mixin.client.gui.screens.inventory;

import com.lovetropics.extras.ExtraDataComponents;
import com.lovetropics.extras.client.screen.container.LootInventoryScreenUnderlay;
import com.lovetropics.extras.template.item.modifers.STTLootCategoryDisplayData;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * LTExtras
 * FIRE EMOJI, FIRE EMOJI, FIRE EMOJI
 */
@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin {

    @Inject(method = "renderSlotContents", at = @At("HEAD"))
    protected void renderSlotContents(GuiGraphicsExtractor graphics, ItemStack itemStack, Slot slot, String itemCount, CallbackInfo ci) {
        if (itemStack.get(ExtraDataComponents.STT_LOOT_CATEGORY_DISPLAY) instanceof STTLootCategoryDisplayData displayData) {
            LootInventoryScreenUnderlay.injectUnderlay(graphics, displayData, slot);
        }
    }
}
