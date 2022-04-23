package com.lovetropics.extras.mixin.perf;

import com.lovetropics.extras.perf.LossyChunkCache;
import com.mojang.datafixers.util.Either;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.server.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Reduce allocations, use a larger and quicker cache, avoid doing unnecessary work when we're just querying chunk
 * and not fully loading it
 */
@Mixin(ServerChunkProvider.class)
public abstract class ServerChunkProviderMixin {
	@Shadow
	@Final
	public ChunkManager chunkMap;
	@Shadow
	@Final
	private Thread mainThread;
	@Shadow
	@Final
	private ServerChunkProvider.ChunkExecutor mainThreadProcessor;
	@Shadow
	@Final
	private TicketManager distanceManager;
	@Shadow
	@Final
	public ServerWorld level;

	@Unique
	private final LossyChunkCache fastCache = new LossyChunkCache(32);

	@Inject(method = "clearCache", at = @At("HEAD"))
	private void invalidateCaches(CallbackInfo ci) {
		this.fastCache.clear();
	}

	/**
	 * @reason avoid allocations and attempt much quicker paths for loading where possible
	 * @author Gegy
	 */
	@Nullable
	@Overwrite
	public IChunk getChunk(int x, int z, ChunkStatus status, boolean load) {
		if (load) {
			if (Thread.currentThread() != this.mainThread) {
				return this.getOrLoadChunkOffThread(x, z, status);
			} else {
				return this.getOrLoadChunkOnThread(x, z, status);
			}
		} else {
			return this.getExistingChunk(x, z, status);
		}
	}

	private IChunk getOrLoadChunkOnThread(int x, int z, ChunkStatus status) {
		// first we test if the chunk already exists in our small cache
		IChunk cached = this.fastCache.get(x, z, status);
		if (cached != null) {
			return cached;
		}

		// if it does not exist, try load it from the chunk entry
		ChunkHolder holder = this.getVisibleChunkIfPresent(ChunkPos.asLong(x, z));
		IChunk chunk = this.getExistingChunkFor(holder, status);

		// the chunk is not ready, we must spawn and join the chunk future
		if (chunk == null) {
			Either<IChunk, ChunkHolder.IChunkLoadingError> result = this.joinFuture(this.loadChunk(x, z, status));

			chunk = result.left().orElse(null);
			if (chunk == null) {
				throw new IllegalStateException("Chunk not there when requested: " + result.right().orElse(null));
			}
		}

		this.fastCache.put(x, z, status, chunk);

		return chunk;
	}

	private <T> T joinFuture(CompletableFuture<T> future) {
		// avoid the lambda allocation if the future is already complete anyway
		if (!future.isDone()) {
			this.mainThreadProcessor.managedBlock(future::isDone);
		}
		return future.join();
	}

	private IChunk getOrLoadChunkOffThread(int x, int z, ChunkStatus status) {
		Either<IChunk, ChunkHolder.IChunkLoadingError> result = CompletableFuture.supplyAsync(
				() -> this.loadChunk(x, z, status),
				this.mainThreadProcessor
		).join().join();

		IChunk chunk = result.left().orElse(null);
		if (chunk != null) {
			return chunk;
		} else {
			throw new IllegalStateException("Chunk not there when requested: " + result.right().orElse(null));
		}
	}

	private CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> loadChunk(int x, int z, ChunkStatus status) {
		long chunkKey = ChunkPos.asLong(x, z);
		ChunkHolder holder = this.getVisibleChunkIfPresent(chunkKey);

		int level = getLevelForStatus(status);
		ChunkPos chunkPos = new ChunkPos(x, z);
		this.distanceManager.addTicket(TicketType.UNKNOWN, chunkPos, level, chunkPos);

		if (!isValidForLevel(holder, level)) {
			// tick the ticket manager to flush the new chunks from adding the ticket
			this.runDistanceManagerUpdates();
			holder = this.getVisibleChunkIfPresent(chunkKey);

			if (!isValidForLevel(holder, level)) {
				throw new IllegalStateException("No chunk holder after ticket has been added");
			}
		}

		return holder.getOrScheduleFuture(status, this.chunkMap);
	}

	/**
	 * @reason redirect to optimized chunk access path
	 * @author Gegy
	 */
	@Overwrite
	@Nullable
	public Chunk getChunkNow(int x, int z) {
		return (Chunk) this.getExistingChunk(x, z, ChunkStatus.FULL);
	}

	private IChunk getExistingChunk(int x, int z, ChunkStatus status) {
		if (Thread.currentThread() != this.mainThread) {
			return this.loadExistingChunk(x, z, status);
		}

		IChunk cached = this.fastCache.get(x, z, status);
		if (cached != null) {
			return cached;
		}

		IChunk chunk = this.loadExistingChunk(x, z, status);
		this.fastCache.put(x, z, status, chunk);

		return chunk;
	}

	@Nullable
	private IChunk loadExistingChunk(int x, int z, ChunkStatus status) {
		ChunkHolder holder = this.getVisibleChunkIfPresent(ChunkPos.asLong(x, z));
		return this.getExistingChunkFor(holder, status);
	}

	@Nullable
	private IChunk getExistingChunkFor(@Nullable ChunkHolder holder, ChunkStatus status) {
		if (isValidForStatus(holder, status)) {
			CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> future = holder.getFutureIfPresentUnchecked(status);
			return future.getNow(ChunkHolder.UNLOADED_CHUNK).left().orElse(null);
		}
		return null;
	}

	/**
	 * @reason replace with implementation that will not return true for partially loaded chunks
	 * @author Gegy
	 */
	@Overwrite
	public boolean hasChunk(int x, int z) {
		return this.getExistingChunk(x, z, ChunkStatus.FULL) != null;
	}

	/**
	 * @reason avoid optional allocation
	 * @author Gegy
	 */
	@Overwrite
	private boolean checkChunkFuture(long pos, Function<ChunkHolder, CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>>> function) {
		ChunkHolder holder = this.getVisibleChunkIfPresent(pos);
		return holder != null && !function.apply(holder).getNow(ChunkHolder.UNLOADED_LEVEL_CHUNK).right().isPresent();
	}

	private static boolean isValidForStatus(ChunkHolder holder, ChunkStatus status) {
		return holder != null && holder.getTicketLevel() <= getLevelForStatus(status);
	}

	private static int getLevelForStatus(ChunkStatus status) {
		return 33 + ChunkStatus.getDistance(status);
	}

	private static boolean isValidForLevel(@Nullable ChunkHolder holder, int level) {
		return holder != null && holder.getTicketLevel() <= level;
	}

	@Shadow
	@Nullable
	protected abstract ChunkHolder getVisibleChunkIfPresent(long pos);

	@Shadow
	protected abstract boolean runDistanceManagerUpdates();
}
