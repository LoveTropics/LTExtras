package com.lovetropics.extras.item;

import com.google.common.base.Suppliers;
import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.client.entity.model.HighHeelsModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Supplier;

public class HighHeelsItem extends Item {
    public HighHeelsItem(Properties properties) {
        super(properties);
    }

    public static class ClientExtensions implements IClientItemExtensions {
        private static final ResourceLocation TEXTURE = LTExtras.location("textures/entity/high_heels.png");

        private final Supplier<HighHeelsModel> model;

        public ClientExtensions() {
            model = Suppliers.memoize(() -> new HighHeelsModel(Minecraft.getInstance().getEntityModels().bakeLayer(HighHeelsModel.LAYER_LOCATION)));
        }

        @Override
        public Model getHumanoidArmorModel(ItemStack itemStack, EquipmentClientInfo.LayerType layerType, Model original) {
            return model.get();
        }

        @Override
        public ResourceLocation getArmorTexture(ItemStack stack, EquipmentClientInfo.LayerType type, EquipmentClientInfo.Layer layer, ResourceLocation _default) {
            return TEXTURE;
        }
    }
}
