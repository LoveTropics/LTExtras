package com.lovetropics.extras.mixin.client.fix;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.ClientCommandHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

// Fix a Neo bug where the ask_server SuggestionProvider is not correctly identified for client commands
// Upstream PR: https://github.com/neoforged/NeoForge/pull/2742
@Mixin(ClientCommandHandler.class)
public class ClientCommandHandlerMixin {
    @WrapOperation(method = "lambda$mergeServerCommands$2", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/synchronization/SuggestionProviders;getProvider(Lnet/minecraft/resources/ResourceLocation;)Lcom/mojang/brigadier/suggestion/SuggestionProvider;"))
    private static SuggestionProvider<SharedSuggestionProvider> getProvider(ResourceLocation name, Operation<SuggestionProvider<SharedSuggestionProvider>> original) {
        if (name.getNamespace().equals(ResourceLocation.DEFAULT_NAMESPACE) && name.getPath().equals("ask_server")) {
            return SuggestionProviders.ASK_SERVER;
        }
        return original.call(name);
    }
}
