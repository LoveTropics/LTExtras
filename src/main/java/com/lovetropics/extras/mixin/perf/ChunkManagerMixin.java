package com.lovetropics.extras.mixin.perf;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ChunkMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

// what!! would you look at the time! it's TACS optimisation time!
@Mixin(ChunkMap.class)
public class ChunkManagerMixin {
	@Inject(
			method = "move",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/core/SectionPos;x()I"),
			locals = LocalCapture.CAPTURE_FAILHARD,
			cancellable = true
	)
	private void onUpdatePlayerPosition(
			ServerPlayer player, CallbackInfo ci,
			int newChunkX, int newChunkZ,
			SectionPos lastSection, SectionPos newSection,
			long lastChunk, long newChunk,
			boolean lastCannotGenerate, boolean newCannotGenerate, boolean changedSection
	) {
		// let's not do chunk tracking for every move packet :)
		if (lastChunk == newChunk) {
			ci.cancel();
		}
	}
}
