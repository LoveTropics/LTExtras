package com.lovetropics.extras.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;

public class BouyBlockItem extends BlockItem {

	public BouyBlockItem(Block blockIn, Properties builder) {
		super(blockIn, builder);
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		RayTraceResult raytraceresult = rayTrace(context.getWorld(), context.getPlayer(), RayTraceContext.FluidMode.SOURCE_ONLY);
		if (raytraceresult.getType() == Type.BLOCK && context.getWorld().getFluidState(((BlockRayTraceResult)raytraceresult).getPos()).getFluid() == Fluids.WATER) {
			return ActionResultType.PASS;
		}
		return super.onItemUse(context);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		// Copied from LilyPadItem
		ItemStack itemstack = playerIn.getHeldItem(handIn);
		RayTraceResult raytraceresult = rayTrace(worldIn, playerIn, RayTraceContext.FluidMode.SOURCE_ONLY);
		if (raytraceresult.getType() == RayTraceResult.Type.MISS) {
			return ActionResult.resultPass(itemstack);
		} else {
			if (raytraceresult.getType() == RayTraceResult.Type.BLOCK) {
				BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult) raytraceresult;
				BlockPos blockpos = blockraytraceresult.getPos();
				Direction direction = blockraytraceresult.getFace();
				if (!worldIn.isBlockModifiable(playerIn, blockpos)
						|| !playerIn.canPlayerEdit(blockpos.offset(direction), direction, itemstack)) {
					return ActionResult.resultFail(itemstack);
				}

				BlockPos blockpos1 = blockpos.up();
				BlockState blockstate = worldIn.getBlockState(blockpos);
				Material material = blockstate.getMaterial();
				FluidState ifluidstate = worldIn.getFluidState(blockpos);
				if ((ifluidstate.getFluid() == Fluids.WATER || material == Material.ICE)
						&& worldIn.isAirBlock(blockpos1)) {

					// special case for handling block placement with water lilies
					net.minecraftforge.common.util.BlockSnapshot blocksnapshot = net.minecraftforge.common.util.BlockSnapshot
							.create(worldIn.getDimensionKey(), worldIn, blockpos1);
					worldIn.setBlockState(blockpos1, getBlock().getDefaultState(), 11);
					if (net.minecraftforge.event.ForgeEventFactory.onBlockPlace(playerIn, blocksnapshot,
							net.minecraft.util.Direction.UP)) {
						blocksnapshot.restore(true, false);
						return ActionResult.resultFail(itemstack);
					}

					if (playerIn instanceof ServerPlayerEntity) {
						CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity) playerIn, blockpos1, itemstack);
					}

					if (!playerIn.abilities.isCreativeMode) {
						itemstack.shrink(1);
					}

					playerIn.addStat(Stats.ITEM_USED.get(this));
					worldIn.playSound(playerIn, blockpos, getPlaceSound(getBlock().getDefaultState(), worldIn, blockpos1, playerIn), SoundCategory.BLOCKS, 1.0F, 1.0F);
					return ActionResult.resultSuccess(itemstack);
				}
			}

			return ActionResult.resultFail(itemstack);
		}
	}
}
