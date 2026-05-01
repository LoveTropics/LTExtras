package com.lovetropics.extras.item;

import com.google.common.base.Suppliers;
import com.lovetropics.extras.ExtraDataComponents;
import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.client.entity.model.HighHeelsModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.event.entity.EntityEvent;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;

import java.util.function.Supplier;

@EventBusSubscriber(modid = LTExtras.MODID)
public class HighHeelsItem extends Item {
    public HighHeelsItem(Properties properties) {
        super(properties);
    }

    @SubscribeEvent
    public static void onEquip(LivingEquipmentChangeEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (event.getTo().has(ExtraDataComponents.ADJUST_HEIGHT) || event.getFrom().has(ExtraDataComponents.ADJUST_HEIGHT)) {
            Pose pose = livingEntity.getPose();
            livingEntity.setPose(Pose.CROUCHING); // Trigger size recalculation
            livingEntity.setPose(pose);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onSize(EntityEvent.Size event) {
        Entity entity = event.getEntity();
        if (entity.isAddedToLevel() && entity instanceof LivingEntity livingEntity) {
            float adjustedHeight = 0.0F;
            for (EquipmentSlot equipmentslot : EquipmentSlotGroup.ARMOR) {
                ItemStack armorItem = livingEntity.getItemBySlot(equipmentslot);
                if (!armorItem.isEmpty()) {
                    adjustedHeight += armorItem.getOrDefault(ExtraDataComponents.ADJUST_HEIGHT, 0.0F);
                }
            }
            if (adjustedHeight != 0.0F) {
                EntityDimensions dimensions = event.getNewSize();
                EntityDimensions newDimensions = new EntityDimensions(
                        dimensions.width(),
                        Mth.clamp(dimensions.height() + adjustedHeight, 0.0F, Float.MAX_VALUE),
                        Mth.clamp(dimensions.eyeHeight() + adjustedHeight, 0.0F, Float.MAX_VALUE),
                        dimensions.attachments(),
                        dimensions.fixed()
                );
                event.setNewSize(newDimensions);
            }
        }
    }

    public static class ClientExtensions implements IClientItemExtensions {
        private static final Identifier TEXTURE = LTExtras.location("textures/entity/high_heels.png");

        private final Supplier<HighHeelsModel> model;

        public ClientExtensions() {
            model = Suppliers.memoize(() -> new HighHeelsModel(Minecraft.getInstance().getEntityModels().bakeLayer(HighHeelsModel.LAYER_LOCATION)));
        }

        @Override
        public Model getHumanoidArmorModel(ItemStack itemStack, EquipmentClientInfo.LayerType layerType, Model original) {
            return model.get();
        }

        @Override
        public Identifier getArmorTexture(ItemStack stack, EquipmentClientInfo.LayerType type, EquipmentClientInfo.Layer layer, Identifier _default) {
            return TEXTURE;
        }
    }
}
