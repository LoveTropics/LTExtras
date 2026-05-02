package com.lovetropics.extras.client.item;

import com.lovetropics.extras.client.ExtraArmPoses;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jspecify.annotations.Nullable;

public class BoxClientExtensions implements IClientItemExtensions {
    @Override
    public HumanoidModel.@Nullable ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand, ItemStack itemStack) {
        return ExtraArmPoses.BOX_PROXY.getValue();
    }
}
