package com.lovetropics.extras.mixin;

import com.google.gson.JsonElement;
import com.lovetropics.extras.template.TemplateConfigs;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.Validatable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ReloadableServerRegistries.class)
public class ReloadableServerRegistriesMixin {
    @Inject(method = "scheduleRegistryLoad", at = @At("RETURN"), cancellable = true)
    @SuppressWarnings("unchecked")
    private static <T extends Validatable> void loadLootTables(LootDataType<T> type, RegistryOps<JsonElement> ops, ResourceManager manager, Executor taskExecutor, CallbackInfoReturnable<CompletableFuture<WritableRegistry<?>>> cir) {
        if (type != LootDataType.TABLE) {
            return;
        }
        CompletableFuture<WritableRegistry<LootTable>> oldFuture = (CompletableFuture<WritableRegistry<LootTable>>) (CompletableFuture<?>) cir.getReturnValue();
        CompletableFuture<WritableRegistry<LootTable>> newFuture = TemplateConfigs.decorateAndLoad(oldFuture, manager, ops, taskExecutor);
        cir.setReturnValue((CompletableFuture<WritableRegistry<?>>) (CompletableFuture<?>) newFuture);
    }
}
