package com.lovetropics.extras.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.lovetropics.extras.ExtraTags;
import com.lovetropics.extras.effect.ExtraEffects;
import com.lovetropics.extras.gamerules.ExtraGamerules;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    private LivingEntityMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Shadow
    public abstract boolean hasEffect(Holder<MobEffect> effect);

    @Shadow
    protected abstract Holder<SoundEvent> getEquipSound(EquipmentSlot slot, ItemStack stack, Equippable equippable);

    @ModifyConstant(method = "handleRelativeFrictionAndCalculateMovement", constant = @Constant(doubleValue = 0.2))
    private double modifyClimbSpeed(double speed) {
        BlockState state = getInBlockState();
        if (state.is(ExtraTags.Blocks.CLIMBABLE_VERY_FAST)) {
            return speed * 2.0;
        } else if (state.is(ExtraTags.Blocks.CLIMBABLE_FAST)) {
            return speed * 1.5;
        }
        return speed;
    }

    @Override
    public boolean isPushedByFluid(FluidType type) {
        if (type == NeoForgeMod.WATER_TYPE.value() && hasEffect(ExtraEffects.FISH_EYE)) {
            return false;
        }
        return super.isPushedByFluid(type);
    }

    @Inject(method = "isPushable", at= @At("HEAD"), cancellable = true)
    private void isPushable(CallbackInfoReturnable<Boolean> cir) {
        if(this.entityTags().contains(ExtraTags.NO_PUSH)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "onEquipItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playSeededSound(Lnet/minecraft/world/entity/Entity;DDDLnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;FFJ)V"), cancellable = true)
    public void onOnEquipItem(EquipmentSlot slot, ItemStack oldStack, ItemStack stack, CallbackInfo ci, @Local(name = "equippable") Equippable equippable) {
        if (level() instanceof ServerLevel serverLevel && (Object) this instanceof ServerPlayer player) {
            if (serverLevel.getGameRules().get(ExtraGamerules.DISABLE_GLOBAL_EQUIP_SOUND.get())) {
                ci.cancel();
                player.connection.send(new ClientboundSoundEntityPacket(
                        this.getEquipSound(slot, stack, equippable),
                        this.getSoundSource(),
                        this,
                        1.0F,
                        1.0F,
                        serverLevel.getRandom().nextLong()
                ));
            }
        }
    }
}
