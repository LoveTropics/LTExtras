package com.lovetropics.extras.mixin;

import com.lovetropics.extras.LTExtras;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.extensions.IBlockStateExtension;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IBlockStateExtension.class)
public interface BlockStateExtensionMixin {
    @Inject(at = @At("HEAD"), method = "getFriction", cancellable = true)
    private void applyFrictionAttribute(LevelReader level, BlockPos pos, @Nullable Entity entity, CallbackInfoReturnable<Float> cir) {
        if (entity instanceof Player player) {
            var thiz = ((BlockState) this);
            cir.setReturnValue(
                    thiz.getBlock().getFriction(thiz, level, pos, entity) * (float) player.getAttributeValue(LTExtras.FRICTION)
            );
        }
    }
}
