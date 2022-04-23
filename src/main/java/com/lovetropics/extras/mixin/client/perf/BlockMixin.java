package com.lovetropics.extras.mixin.client.perf;

import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Block.class)
public class BlockMixin {
    @Shadow
    @Final
    private static ThreadLocal<Object2ByteLinkedOpenHashMap<Block.BlockStatePairKey>> OCCLUSION_CACHE;

    /**
     * @reason add fast path for simple full cubes
     * @author Gegy
     */
    @Overwrite
    public static boolean shouldRenderFace(BlockState state, BlockGetter world, BlockPos pos, Direction direction) {
        BlockPos adjacentPos = pos.relative(direction);
        BlockState adjacentState = world.getBlockState(adjacentPos);
        if (state.skipRendering(adjacentState, direction)) {
            return false;
        } else if (!adjacentState.canOcclude()) {
            return true;
        }

        VoxelShape shape = state.getOcclusionShape(world, pos);

        // an empty shape will never intersect with another face
        if (shape == Shapes.empty()) return true;

        VoxelShape adjacentShape = adjacentState.getOcclusionShape(world, adjacentPos);

        // an empty shape will never intersect with another face
        if (adjacentShape == Shapes.empty()) return true;

        // two full cubes will always occlude each other
        if (shape == Shapes.block() && adjacentShape == Shapes.block()) {
            return false;
        }

        Block.BlockStatePairKey cacheKey = new Block.BlockStatePairKey(state, adjacentState, direction);
        Object2ByteLinkedOpenHashMap<Block.BlockStatePairKey> cache = OCCLUSION_CACHE.get();
        byte cacheResult = cache.getAndMoveToFirst(cacheKey);
        if (cacheResult != 127) {
            return cacheResult != 0;
        }

        VoxelShape faceShape = Shapes.getFaceShape(shape, direction);
        VoxelShape adjacentFaceShape = Shapes.getFaceShape(adjacentShape, direction.getOpposite());
        boolean visible = Shapes.joinIsNotEmpty(faceShape, adjacentFaceShape, BooleanOp.ONLY_FIRST);

        if (cache.size() == 2048) {
            cache.removeLastByte();
        }

        cache.putAndMoveToFirst(cacheKey, (byte) (visible ? 1 : 0));

        return visible;
    }
}
