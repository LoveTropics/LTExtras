package com.lovetropics.extras.block;

import com.lovetropics.extras.client.particle.ExtraParticles;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

import java.util.List;
import java.util.Random;

public class CheckpointBlock extends Block {

	public static final IntegerProperty STAGE = IntegerProperty.create("stage", 1, 40);

	public CheckpointBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(STAGE);
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
		return true;
	}

	@Override
	@Deprecated
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		if (worldIn instanceof World && ((World) worldIn).isRemote) {
			if (context.getEntity() instanceof PlayerEntity && !((PlayerEntity)context.getEntity()).isCreative()) {
				return VoxelShapes.empty();
			}
		}

		return super.getShape(state, worldIn, pos, context);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return VoxelShapes.empty();
	}

	@Override
	public void addInformation(ItemStack stack, IBlockReader worldIn, List<ITextComponent> tooltip,
			ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		tooltip.add(new StringTextComponent("Stage: ")
				.appendSibling(new StringTextComponent(Integer.toString(getStage(stack)))
						.mergeStyle(TextFormatting.AQUA)));
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return super.getStateForPlacement(context).with(STAGE, getStage(context.getItem()));
	}

	private int getStage(ItemStack stack) {
		CompoundNBT tag = stack.getTag();
		if (tag != null && tag.contains("stage", Constants.NBT.TAG_INT)) {
			return tag.getInt("stage");
		}
		return 1;
	}

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, World world, BlockPos pos, Random random) {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        if (this.isHoldingBarrier(player)) {
            world.addParticle(ExtraParticles.CHECKPOINT.get(), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0.0, 0.0, 0.0);
        }
    }

    private boolean isHoldingBarrier(PlayerEntity player) {
        Item item = this.asItem();
        return player.getHeldItemMainhand().getItem() == item
                || player.getHeldItemOffhand().getItem() == item;
    }
}
