package com.lovetropics.extras.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;

public class BouyBlockItem extends BlockItem {

	public BouyBlockItem(Block blockIn, Properties builder) {
		super(blockIn, builder);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		HitResult raytraceresult = getPlayerPOVHitResult(context.getLevel(), context.getPlayer(), ClipContext.Fluid.SOURCE_ONLY);
		if (raytraceresult.getType() == Type.BLOCK && context.getLevel().getFluidState(((BlockHitResult)raytraceresult).getBlockPos()).getType() == Fluids.WATER) {
			return InteractionResult.PASS;
		}
		return super.useOn(context);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
		// Copied from LilyPadItem
		ItemStack itemstack = playerIn.getItemInHand(handIn);
		HitResult raytraceresult = getPlayerPOVHitResult(worldIn, playerIn, ClipContext.Fluid.SOURCE_ONLY);
		if (raytraceresult.getType() == HitResult.Type.MISS) {
			return InteractionResultHolder.pass(itemstack);
		} else {
			if (raytraceresult.getType() == HitResult.Type.BLOCK) {
				BlockHitResult blockraytraceresult = (BlockHitResult) raytraceresult;
				BlockPos blockpos = blockraytraceresult.getBlockPos();
				Direction direction = blockraytraceresult.getDirection();
				if (!worldIn.mayInteract(playerIn, blockpos)
						|| !playerIn.mayUseItemAt(blockpos.relative(direction), direction, itemstack)) {
					return InteractionResultHolder.fail(itemstack);
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
							net.minecraft.core.Direction.UP)) {
						blocksnapshot.restore(true, false);
						return InteractionResultHolder.fail(itemstack);
					}

					if (playerIn instanceof ServerPlayer) {
						CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer) playerIn, blockpos1, itemstack);
					}

					if (!playerIn.getAbilities().instabuild) {
						itemstack.shrink(1);
					}

					playerIn.awardStat(Stats.ITEM_USED.get(this));
					worldIn.playSound(playerIn, blockpos, getPlaceSound(getBlock().defaultBlockState(), worldIn, blockpos1, playerIn), SoundSource.BLOCKS, 1.0F, 1.0F);
					return InteractionResultHolder.success(itemstack);
				}
			}

			return InteractionResultHolder.fail(itemstack);
		}
	}
}
