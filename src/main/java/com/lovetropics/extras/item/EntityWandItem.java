package com.lovetropics.extras.item;

import com.lovetropics.extras.block.entity.MobControllerBlockEntity;
import com.lovetropics.extras.entity.ExtendedCreatureEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.Level;

import net.minecraft.world.item.Item.Properties;

public class EntityWandItem extends Item {
    public EntityWandItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player playerIn, LivingEntity target, InteractionHand hand) {
        if (!playerIn.level.isClientSide()) {
            if (target instanceof ExtendedCreatureEntity) {
                int id = target.getId();
                ItemStack stack1 = playerIn.getItemInHand(hand);
                stack1.getOrCreateTag().putInt("EntityId", id);
                playerIn.displayClientMessage(new TextComponent("Targeted entity!"), true);
                return InteractionResult.SUCCESS;
            } else {
                playerIn.displayClientMessage(new TextComponent("This entity cannot be targeted!"), true);
            }
        }

        return super.interactLivingEntity(stack, playerIn, target, hand);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPos pos = context.getClickedPos();
        Level world = context.getLevel();
        ItemStack stack = context.getItemInHand();

        if (!world.isClientSide()) {
            BlockEntity te = world.getBlockEntity(pos);

            if (te instanceof MobControllerBlockEntity) {
                MobControllerBlockEntity mobController = (MobControllerBlockEntity) te;
                CompoundTag nbt = stack.getTag();

                if (nbt != null && nbt.contains("EntityId")) {
                    int id = nbt.getInt("EntityId");

                    Entity entity = world.getEntity(id);
                    if (entity != null) {
                        mobController.addEntity(entity);
                        context.getPlayer().displayClientMessage(new TextComponent("Added entity!"), true);
                    }
                }
            }
        }

        return super.useOn(context);
    }
}
