package com.lovetropics.extras.mixin;

import com.lovetropics.extras.entity.ForkliftEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.class)
public class BlockBehaviourMixin {

    @Unique
    private static final VoxelShape HACK_SHAPE = Block.cube(16.5);

    @Inject(method = "getCollisionShape", at = @At("HEAD"), cancellable = true)
    public void onGetCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (context instanceof net.minecraft.world.phys.shapes.EntityCollisionContext ctx) {
            Entity entity = ctx.getEntity();
            if (entity instanceof ForkliftEntity forkliftEntity) {
                if (forkliftEntity.shouldDestroyBlock(pos, state)) {
                    forkliftEntity.destroyBlock(state, entity.level(), pos);
                    cir.setReturnValue(Shapes.empty());
                }
            }
        }
    }


}

