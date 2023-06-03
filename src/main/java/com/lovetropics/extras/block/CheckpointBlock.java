package com.lovetropics.extras.block;

import com.lovetropics.extras.client.particle.ExtraParticles;
import net.minecraft.nbt.Tag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Random;

public class CheckpointBlock extends Block {

	public static final IntegerProperty STAGE = IntegerProperty.create("stage", 1, 40);

	public CheckpointBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(STAGE);
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
		return true;
	}

	@Override
	@Deprecated
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		if (worldIn instanceof Level level && level.isClientSide()) {
			if (context instanceof EntityCollisionContext entityContext) {
				if (entityContext.getEntity() instanceof Player player && !player.isCreative()) {
					return Shapes.empty();
				}
			}
		}

		return super.getShape(state, worldIn, pos, context);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return Shapes.empty();
	}

	@Override
	public void appendHoverText(ItemStack stack, BlockGetter worldIn, List<Component> tooltip,
			TooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		tooltip.add(Component.literal("Stage: ")
				.append(Component.literal(Integer.toString(getStage(stack)))
						.withStyle(ChatFormatting.AQUA)));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return super.getStateForPlacement(context).setValue(STAGE, getStage(context.getItemInHand()));
	}

	private int getStage(ItemStack stack) {
		CompoundTag tag = stack.getTag();
		if (tag != null && tag.contains("stage", Tag.TAG_INT)) {
			return tag.getInt("stage");
		}
		return 1;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null) {
			return;
		}

		if (this.isHoldingBarrier(player)) {
			world.addParticle(ExtraParticles.CHECKPOINT.get(), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0.0, 0.0, 0.0);
		}
	}

	private boolean isHoldingBarrier(Player player) {
		Item item = this.asItem();
		return player.getMainHandItem().getItem() == item
				|| player.getOffhandItem().getItem() == item;
	}
}
