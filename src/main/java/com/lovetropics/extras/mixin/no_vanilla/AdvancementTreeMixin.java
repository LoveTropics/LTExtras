package com.lovetropics.extras.mixin.no_vanilla;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementTree;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.List;

@Mixin(AdvancementTree.class)
public class AdvancementTreeMixin {

    @Inject(method = "addAll", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;)V"))
    public void disableVanillaAdvancementsLog(Collection<AdvancementHolder> advancements, CallbackInfo ci, @Local List<AdvancementHolder> list) {
        list.removeIf(advancementHolder -> advancementHolder.id().getPath().contains("recipes"));
    }

}
