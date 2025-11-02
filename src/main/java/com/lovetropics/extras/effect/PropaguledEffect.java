package com.lovetropics.extras.effect;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.client.extensions.common.IClientMobEffectExtensions;

public class PropaguledEffect extends MobEffect {
    public PropaguledEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    public PropaguledEffect addAttributeModifier(Holder<Attribute> attribute, ResourceLocation id, double amount, AttributeModifier.Operation operation) {
        return (PropaguledEffect) super.addAttributeModifier(attribute, id, amount, operation);
    }

    public static class ClientExtensions implements IClientMobEffectExtensions {
        private static final ItemStack PROPAGULE_ITEMSTACK = Items.MANGROVE_PROPAGULE.getDefaultInstance();

        @Override
        public boolean renderGuiIcon(MobEffectInstance instance, Gui gui, GuiGraphics guiGraphics, int x, int y, float z, float alpha) {
            guiGraphics.renderFakeItem(PROPAGULE_ITEMSTACK, x + 4, y + 3);
            return true;
        }
    }
}
