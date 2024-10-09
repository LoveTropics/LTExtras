package com.lovetropics.extras.block;

import com.lovetropics.extras.ExtraBlocks;
import com.lovetropics.extras.block.entity.SeatEntity;
import com.lovetropics.extras.entity.ExtraEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SeatBlock extends SlabBlock {

    public static final MapCodec<SeatBlock> CODEC = simpleCodec(SeatBlock::new);

    public SeatBlock(final Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(final BlockState state, final Level level, final BlockPos pos, final Player player, final BlockHitResult hitResult) {
        if (player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        final List<SeatEntity> foundSeats = level.getEntitiesOfClass(SeatEntity.class, new AABB(pos));

        if (!foundSeats.isEmpty() && !foundSeats.stream().allMatch(seat -> seat.getPassengers().isEmpty())) {
            return InteractionResult.FAIL;
        }

        final SeatEntity seatEntity = new SeatEntity(ExtraEntities.SEAT.get(), level);
        seatEntity.setPos(pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f);
        level.addFreshEntity(seatEntity);
        player.startRiding(seatEntity);

        return InteractionResult.SUCCESS;
    }

    @Override
    protected RenderShape getRenderShape(final BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public MapCodec<? extends SlabBlock> codec() {
        return CODEC;
    }
}
