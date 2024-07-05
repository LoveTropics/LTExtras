package com.lovetropics.extras.item;

import com.lovetropics.extras.ExtraDataComponents;
import com.lovetropics.extras.block.entity.MobControllerBlockEntity;
import com.lovetropics.extras.entity.ExtendedCreatureEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class EntityWandItem extends Item {
	public EntityWandItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (player.level().isClientSide()) {
            return super.interactLivingEntity(stack, player, target, hand);
        }

        if (target instanceof ExtendedCreatureEntity) {
            ItemStack heldItem = player.getItemInHand(hand);
            heldItem.set(ExtraDataComponents.TARGETED_ENTITY, target.getUUID());
            player.displayClientMessage(Component.literal("Targeted entity!"), true);
            return InteractionResult.SUCCESS;
        } else {
            player.displayClientMessage(Component.literal("This entity cannot be targeted!"), true);
        }

        return super.interactLivingEntity(stack, player, target, hand);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		if (!(level instanceof ServerLevel serverLevel)) {
			return super.useOn(context);
		}

		BlockPos pos = context.getClickedPos();
		ItemStack stack = context.getItemInHand();

        if (level.getBlockEntity(pos) instanceof MobControllerBlockEntity mobController) {
            UUID entityId = stack.get(ExtraDataComponents.TARGETED_ENTITY);
            if (entityId != null) {
                Entity entity = serverLevel.getEntity(entityId);
                if (entity != null) {
                    mobController.addEntity(entity);
                    context.getPlayer().displayClientMessage(Component.literal("Added entity!"), true);
                }
            }
        }

        return super.useOn(context);
	}
}
