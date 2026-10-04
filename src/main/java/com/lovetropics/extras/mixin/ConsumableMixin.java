package com.lovetropics.extras.mixin;

import com.lovetropics.extras.ExtraDataComponents;
import com.lovetropics.extras.consume_actions.ConsumeAction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Consumable.class)
public class ConsumableMixin {

    @Inject(method = "onConsume", at = @At("HEAD"))
    public void onOnConsume(Level level, LivingEntity user, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        if (user instanceof ServerPlayer player) {
            ConsumeAction consumeAction = stack.get(ExtraDataComponents.CONSUME_ACTION);
            if (consumeAction != null) {
                consumeAction.onConsume(player, stack);
            }
        }
    }
}
