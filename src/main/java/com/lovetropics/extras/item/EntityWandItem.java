package com.lovetropics.extras.item;

import com.lovetropics.extras.block.entity.MobControllerBlockEntity;
import com.lovetropics.extras.entity.ExtendedCreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import net.minecraft.item.Item.Properties;

public class EntityWandItem extends Item {
    public EntityWandItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType interactLivingEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
        if (!playerIn.level.isClientSide()) {
            if (target instanceof ExtendedCreatureEntity) {
                int id = target.getId();
                ItemStack stack1 = playerIn.getItemInHand(hand);
                stack1.getOrCreateTag().putInt("EntityId", id);
                playerIn.displayClientMessage(new StringTextComponent("Targeted entity!"), true);
                return ActionResultType.SUCCESS;
            } else {
                playerIn.displayClientMessage(new StringTextComponent("This entity cannot be targeted!"), true);
            }
        }

        return super.interactLivingEntity(stack, playerIn, target, hand);
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        BlockPos pos = context.getClickedPos();
        World world = context.getLevel();
        ItemStack stack = context.getItemInHand();

        if (!world.isClientSide()) {
            TileEntity te = world.getBlockEntity(pos);

            if (te instanceof MobControllerBlockEntity) {
                MobControllerBlockEntity mobController = (MobControllerBlockEntity) te;
                CompoundNBT nbt = stack.getTag();

                if (nbt != null && nbt.contains("EntityId")) {
                    int id = nbt.getInt("EntityId");

                    Entity entity = world.getEntity(id);
                    if (entity != null) {
                        mobController.addEntity(entity);
                        context.getPlayer().displayClientMessage(new StringTextComponent("Added entity!"), true);
                    }
                }
            }
        }

        return super.useOn(context);
    }
}
