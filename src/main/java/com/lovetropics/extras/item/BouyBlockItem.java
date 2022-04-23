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

import net.minecraft.item.Item.Properties;

public class BouyBlockItem extends BlockItem {

	public BouyBlockItem(Block blockIn, Properties builder) {
		super(blockIn, builder);
	}

	@Override
	public ActionResultType useOn(ItemUseContext context) {
		RayTraceResult raytraceresult = getPlayerPOVHitResult(context.getLevel(), context.getPlayer(), RayTraceContext.FluidMode.SOURCE_ONLY);
		if (raytraceresult.getType() == Type.BLOCK && context.getLevel().getFluidState(((BlockRayTraceResult)raytraceresult).getBlockPos()).getType() == Fluids.WATER) {
			return ActionResultType.PASS;
		}
		return super.useOn(context);
	}

	@Override
	public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
		// Copied from LilyPadItem
		ItemStack itemstack = playerIn.getItemInHand(handIn);
		RayTraceResult raytraceresult = getPlayerPOVHitResult(worldIn, playerIn, RayTraceContext.FluidMode.SOURCE_ONLY);
		if (raytraceresult.getType() == RayTraceResult.Type.MISS) {
			return ActionResult.pass(itemstack);
		} else {
			if (raytraceresult.getType() == RayTraceResult.Type.BLOCK) {
				BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult) raytraceresult;
				BlockPos blockpos = blockraytraceresult.getBlockPos();
				Direction direction = blockraytraceresult.getDirection();
				if (!worldIn.mayInteract(playerIn, blockpos)
						|| !playerIn.mayUseItemAt(blockpos.relative(direction), direction, itemstack)) {
					return ActionResult.fail(itemstack);
				}

				BlockPos blockpos1 = blockpos.above();
				BlockState blockstate = worldIn.getBlockState(blockpos);
				Material material = blockstate.getMaterial();
				FluidState ifluidstate = worldIn.getFluidState(blockpos);
				if ((ifluidstate.getType() == Fluids.WATER || material == Material.ICE)
						&& worldIn.isEmptyBlock(blockpos1)) {

					// special case for handling block placement with water lilies
					net.minecraftforge.common.util.BlockSnapshot blocksnapshot = net.minecraftforge.common.util.BlockSnapshot
							.create(worldIn.dimension(), worldIn, blockpos1);
					worldIn.setBlock(blockpos1, getBlock().defaultBlockState(), 11);
					if (net.minecraftforge.event.ForgeEventFactory.onBlockPlace(playerIn, blocksnapshot,
							net.minecraft.util.Direction.UP)) {
						blocksnapshot.restore(true, false);
						return ActionResult.fail(itemstack);
					}

					if (playerIn instanceof ServerPlayerEntity) {
						CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity) playerIn, blockpos1, itemstack);
					}

					if (!playerIn.abilities.instabuild) {
						itemstack.shrink(1);
					}

					playerIn.awardStat(Stats.ITEM_USED.get(this));
					worldIn.playSound(playerIn, blockpos, getPlaceSound(getBlock().defaultBlockState(), worldIn, blockpos1, playerIn), SoundCategory.BLOCKS, 1.0F, 1.0F);
					return ActionResult.success(itemstack);
				}
			}

			return ActionResult.fail(itemstack);
		}
	}
}
