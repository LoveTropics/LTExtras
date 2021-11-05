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

public class EntityWandItem extends Item {
    public EntityWandItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
        if (!playerIn.world.isRemote()) {
            if (target instanceof ExtendedCreatureEntity) {
                int id = target.getEntityId();
                ItemStack stack1 = playerIn.getHeldItem(hand);
                stack1.getOrCreateTag().putInt("EntityId", id);
                playerIn.sendStatusMessage(new StringTextComponent("Targeted entity!"), true);
                return ActionResultType.SUCCESS;
            } else {
                playerIn.sendStatusMessage(new StringTextComponent("This entity cannot be targeted!"), true);
            }
        }

        return super.itemInteractionForEntity(stack, playerIn, target, hand);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        BlockPos pos = context.getPos();
        World world = context.getWorld();
        ItemStack stack = context.getItem();

        if (!world.isRemote()) {
            TileEntity te = world.getTileEntity(pos);

            if (te instanceof MobControllerBlockEntity) {
                MobControllerBlockEntity mobController = (MobControllerBlockEntity) te;
                CompoundNBT nbt = stack.getTag();

                if (nbt != null && nbt.contains("EntityId")) {
                    int id = nbt.getInt("EntityId");

                    Entity entity = world.getEntityByID(id);
                    if (entity != null) {
                        mobController.addEntity(entity);
                        context.getPlayer().sendStatusMessage(new StringTextComponent("Added entity!"), true);
                    }
                }
            }
        }

        return super.onItemUse(context);
    }
}
