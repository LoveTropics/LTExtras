package com.lovetropics.extras.mixin.no_vanilla;

import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin({ServerAdvancementManager.class})
public class DropVanillaAdvancementsMixin {
    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("HEAD"))
    private void load(Map<ResourceLocation, JsonElement> json, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci) {
        json.keySet().removeIf(path -> path.getNamespace().equals("minecraft") && !isAllowed(path));
    }

    // It's important, ok?
    private static boolean isAllowed(ResourceLocation path) {
        return path.getPath().contains("glass");
    }
}
