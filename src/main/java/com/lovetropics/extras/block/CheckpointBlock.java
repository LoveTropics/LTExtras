package com.lovetropics.extras.block;

import com.lovetropics.extras.client.particle.ExtraParticles;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class CheckpointBlock extends Block {
	public static final MapCodec<CheckpointBlock> CODEC = simpleCodec(CheckpointBlock::new);

	public static final IntegerProperty STAGE = IntegerProperty.create("stage", 1, 40);

	public CheckpointBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.defaultBlockState().setValue(STAGE, 1));
	}

	@Override
	protected MapCodec<? extends CheckpointBlock> codec() {
		return CODEC;
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
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
