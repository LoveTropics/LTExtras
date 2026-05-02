package com.lovetropics.extras.block;

import com.lovetropics.extras.ExtraBlocks;
import com.lovetropics.extras.ExtraDataComponents;
import com.lovetropics.extras.block.entity.TeleportPadBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.jspecify.annotations.Nullable;

@EventBusSubscriber
public class TeleportPadBlock extends Block implements EntityBlock, SimpleWaterloggedBlock {
    public static final MapCodec<TeleportPadBlock> CODEC = simpleCodec(TeleportPadBlock::new);

    public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final VoxelShape SHAPE_BOTTOM = Block.column(16.0, 0.0, 8.0);
    private static final VoxelShape SHAPE_TOP = Block.column(16.0, 8.0, 16.0);

    public TeleportPadBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(HALF, Half.BOTTOM).setValue(WATERLOGGED, false));
    }

    @Override
    protected MapCodec<TeleportPadBlock> codec() {
        return CODEC;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.is(Items.DEBUG_STICK) && player.canUseGameMasterBlocks()) {
            if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
                serverPlayer.sendSystemMessage(Component.literal("Right-click with the debug stick at the desired teleporting location"));
                stack.set(ExtraDataComponents.TELEPORT_PAD.get(), pos);
            }
            return InteractionResult.SUCCESS;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        BlockPos teleportPadPos = event.getItemStack().remove(ExtraDataComponents.TELEPORT_PAD.get());
        if (teleportPadPos == null) {
            return;
        }
        Level level = event.getLevel();
        if (!level.isClientSide() && level.getBlockEntity(teleportPadPos) instanceof TeleportPadBlockEntity teleportPad) {
            teleportPad.updateTarget(event.getHitVec().getLocation());
            if (event.getEntity() instanceof ServerPlayer serverPlayer) {
                serverPlayer.sendSystemMessage(Component.literal("Set teleporting location"));
            }
        }
        event.setCancellationResult(InteractionResult.SUCCESS);
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        BlockPos teleportPadPos = event.getItemStack().remove(ExtraDataComponents.TELEPORT_PAD.get());
        if (teleportPadPos == null) {
            return;
        }
        Level level = event.getLevel();
        if (!level.isClientSide() && level.getBlockEntity(teleportPadPos) instanceof TeleportPadBlockEntity teleportPad) {
            teleportPad.updateTarget(event.getEntity().position());
            if (event.getEntity() instanceof ServerPlayer serverPlayer) {
                serverPlayer.sendSystemMessage(Component.literal("Set teleporting location"));
            }
        }
        event.setCancellationResult(InteractionResult.SUCCESS);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (level.isClientSide()) {
            return;
        }

        if (level.getBlockEntity(pos) instanceof TeleportPadBlockEntity teleportPad && level instanceof ServerLevel serverLevel) {
            Vec3 targetPos = teleportPad.getTargetPos();
            if( targetPos == null ) {
                return;
            }
            level.gameEvent(GameEvent.TELEPORT, pos, GameEvent.Context.of(entity));

            // in case we add cross-dimension teleportation, this will handle it correctly
            Entity teleportedEntity = entity.teleport(
                    new TeleportTransition(serverLevel, targetPos, entity.getDeltaMovement(), entity.getYRot(), entity.getXRot(), TeleportTransition.DO_NOTHING)
            );
            if (teleportedEntity != null) {
                teleportedEntity.resetFallDistance();
            }

            level.playSound(null, entity, SoundEvents.PLAYER_TELEPORT, SoundSource.BLOCKS, 1.0f, 1.0f);
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Direction clickedFace = context.getClickedFace();
        boolean onTopHalf = context.getClickLocation().y - pos.getY() > 0.5;
        FluidState fluidstate = context.getLevel().getFluidState(pos);
        return defaultBlockState()
                .setValue(HALF, clickedFace == Direction.DOWN || (clickedFace != Direction.UP && onTopHalf) ? Half.TOP : Half.BOTTOM)
                .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess scheduledTickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        if (state.getValue(WATERLOGGED)) {
            scheduledTickAccess.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        return super.updateShape(state, level, scheduledTickAccess, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(HALF)) {
            case BOTTOM -> SHAPE_BOTTOM;
            case TOP -> SHAPE_TOP;
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HALF, WATERLOGGED);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TeleportPadBlockEntity(ExtraBlocks.TELEPORT_PAD_ENTITY.get(), pos, state);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }
}
