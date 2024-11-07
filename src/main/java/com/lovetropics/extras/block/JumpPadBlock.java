package com.lovetropics.extras.block;

import com.lovetropics.extras.ExtraBlocks;
import com.lovetropics.extras.ExtraDataComponents;
import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.block.entity.JumpPadBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber(modid = LTExtras.MODID)
public class JumpPadBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final MapCodec<JumpPadBlock> CODEC = simpleCodec(JumpPadBlock::new);

    public static final DirectionProperty FACING = DirectionProperty.create("facing", direction -> direction != Direction.DOWN);
    public static final EnumProperty<Half> HALF = EnumProperty.create("half", Half.class);

    private static final VoxelShape BOTTOM_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
    private static final VoxelShape TOP_SHAPE = Block.box(0.0, 8.0, 0.0, 16.0, 16.0, 16.0);

    public JumpPadBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.UP).setValue(HALF, Half.BOTTOM));
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.is(Items.DEBUG_STICK) && player.canUseGameMasterBlocks()) {
            if (!level.isClientSide()) {
                player.sendSystemMessage(Component.literal("Right-click with the debug stick at the desired jump target location"));
                stack.set(ExtraDataComponents.JUMP_PAD.get(), pos);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide());
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        BlockPos jumpPadPos = event.getItemStack().remove(ExtraDataComponents.JUMP_PAD.get());
        if (jumpPadPos == null) {
            return;
        }
        Level level = event.getLevel();
        if (!level.isClientSide() && level.getBlockEntity(jumpPadPos) instanceof JumpPadBlockEntity jumpPad) {
			jumpPad.updateTarget(event.getHitVec().getLocation());
            event.getEntity().sendSystemMessage(Component.literal("Set jump target"));
        }
        event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide()));
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        BlockPos jumpPadPos = event.getItemStack().remove(ExtraDataComponents.JUMP_PAD.get());
        if (jumpPadPos == null) {
            return;
        }
        Level level = event.getLevel();
        if (!level.isClientSide() && level.getBlockEntity(jumpPadPos) instanceof JumpPadBlockEntity jumpPad) {
            jumpPad.updateTarget(event.getEntity().position());
            event.getEntity().sendSystemMessage(Component.literal("Set jump target"));
        }
        event.setCancellationResult(InteractionResult.sidedSuccess(event.getSide().isClient()));
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (level.isClientSide()) {
            return;
        }

        // Only launch once the entity has come to a rest on top of the block, for predictable results
        if (!entity.onGround() || entity.getKnownMovement().lengthSqr() > 0.01 * 0.01) {
            return;
        }

		if (level.getBlockEntity(pos) instanceof JumpPadBlockEntity jumpPad) {
            Vec3 velocity = jumpPad.getLaunchVelocity();
            if (velocity.lengthSqr() < 0.01) {
                return;
            }
            level.playSound(null, entity, SoundEvents.BREEZE_JUMP, SoundSource.BLOCKS, 1.0f, 1.0f);
            entity.setDeltaMovement(velocity);
			if (entity instanceof ServerPlayer serverPlayer) {
				serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(serverPlayer));
			}
		}
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
		Direction clickedFace = context.getClickedFace();
		boolean onTopHalf = context.getClickLocation().y - pos.getY() > 0.5;
		return defaultBlockState().setValue(HALF, clickedFace == Direction.DOWN || (clickedFace != Direction.UP && onTopHalf) ? Half.TOP : Half.BOTTOM);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(HALF)) {
            case BOTTOM -> BOTTOM_SHAPE;
            case TOP -> TOP_SHAPE;
		};
    }

    @Override
    protected MapCodec<JumpPadBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, HALF);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new JumpPadBlockEntity(ExtraBlocks.JUMP_PAD_ENTITY.get(), pos, state);
    }
}
