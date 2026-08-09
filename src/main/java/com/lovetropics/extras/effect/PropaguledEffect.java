package com.lovetropics.extras.effect;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.client.extensions.common.IClientMobEffectExtensions;

public class PropaguledEffect extends MobEffect {
    public PropaguledEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    public PropaguledEffect addAttributeModifier(Holder<Attribute> attribute, Identifier id, double amount, AttributeModifier.Operation operation) {
        return (PropaguledEffect) super.addAttributeModifier(attribute, id, amount, operation);
    }

    public static class ClientExtensions implements IClientMobEffectExtensions {

        @Override
        public boolean extractInventoryIcon(MobEffectInstance instance, AbstractContainerScreen<?> screen, GuiGraphicsExtractor graphics, int x, int y, int width, int height, int color) {
            graphics.fakeItem(Items.MANGROVE_PROPAGULE.getDefaultInstance(), x + 1, y + 6);
            return true;
        }

        @Override
        public boolean extractHudIcon(MobEffectInstance instance, Hud hud, GuiGraphicsExtractor graphics, int x, int y, int width, int height, int color) {
            graphics.fakeItem(Items.MANGROVE_PROPAGULE.getDefaultInstance(), x + 1, y + 6);
            return true;
        }
    }
}
