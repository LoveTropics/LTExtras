package com.lovetropics.extras.mixin.client.perf;

import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Block.class)
public class BlockMixin {
    @Shadow
    @Final
    private static ThreadLocal<Object2ByteLinkedOpenHashMap<Block.RenderSideCacheKey>> OCCLUSION_CACHE;

    /**
     * @reason add fast path for simple full cubes
     * @author Gegy
     */
    @Overwrite
    public static boolean shouldRenderFace(BlockState state, IBlockReader world, BlockPos pos, Direction direction) {
        BlockPos adjacentPos = pos.relative(direction);
        BlockState adjacentState = world.getBlockState(adjacentPos);
        if (state.skipRendering(adjacentState, direction)) {
            return false;
        } else if (!adjacentState.canOcclude()) {
            return true;
        }

        VoxelShape shape = state.getOcclusionShape(world, pos);

        // an empty shape will never intersect with another face
        if (shape == VoxelShapes.empty()) return true;

        VoxelShape adjacentShape = adjacentState.getOcclusionShape(world, adjacentPos);

        // an empty shape will never intersect with another face
        if (adjacentShape == VoxelShapes.empty()) return true;

        // two full cubes will always occlude each other
        if (shape == VoxelShapes.block() && adjacentShape == VoxelShapes.block()) {
            return false;
        }

        Block.RenderSideCacheKey cacheKey = new Block.RenderSideCacheKey(state, adjacentState, direction);
        Object2ByteLinkedOpenHashMap<Block.RenderSideCacheKey> cache = OCCLUSION_CACHE.get();
        byte cacheResult = cache.getAndMoveToFirst(cacheKey);
        if (cacheResult != 127) {
            return cacheResult != 0;
        }

        VoxelShape faceShape = VoxelShapes.getFaceShape(shape, direction);
        VoxelShape adjacentFaceShape = VoxelShapes.getFaceShape(adjacentShape, direction.getOpposite());
        boolean visible = VoxelShapes.joinIsNotEmpty(faceShape, adjacentFaceShape, IBooleanFunction.ONLY_FIRST);

        if (cache.size() == 2048) {
            cache.removeLastByte();
        }

        cache.putAndMoveToFirst(cacheKey, (byte) (visible ? 1 : 0));

        return visible;
    }
}
