package com.lovetropics.extras.mixin.client.perf;

import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraftforge.registries.IRegistryDelegate;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.concurrent.locks.StampedLock;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

// TODO: PR into Forge!
@Mixin(RenderTypeLookup.class)
public class RenderTypeLookupMixin {
    @Shadow private static boolean fancyGraphics;

    @Shadow @Final @Deprecated private static Map<Block, RenderType> TYPES_BY_BLOCK;
    @Shadow @Final @Deprecated private static Map<Fluid, RenderType> TYPES_BY_FLUID;
    @Shadow @Mutable @Final private static Map<IRegistryDelegate<Block>, Predicate<RenderType>> blockRenderChecks;
    @Shadow @Mutable @Final private static Map<IRegistryDelegate<Fluid>, Predicate<RenderType>> fluidRenderChecks;

    @Unique
    private static final StampedLock RENDER_CHECK_LOCK = new StampedLock();

    @Unique
    private static final Predicate<RenderType> SOLID_PREDICATE = type -> type == RenderType.getSolid();

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void init(CallbackInfo ci) {
        Reference2ObjectMap<IRegistryDelegate<Block>, Predicate<RenderType>> blockMap = new Reference2ObjectOpenHashMap<>();
        Reference2ObjectMap<IRegistryDelegate<Fluid>, Predicate<RenderType>> fluidMap = new Reference2ObjectOpenHashMap<>();
        blockMap.defaultReturnValue(SOLID_PREDICATE);
        fluidMap.defaultReturnValue(SOLID_PREDICATE);

        RenderTypeLookupMixin.blockRenderChecks = blockMap;
        RenderTypeLookupMixin.fluidRenderChecks = fluidMap;

        TYPES_BY_BLOCK.forEach(RenderTypeLookup::setRenderLayer);
        TYPES_BY_FLUID.forEach(RenderTypeLookup::setRenderLayer);
    }

    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Ljava/util/Map;forEach(Ljava/util/function/BiConsumer;)V"))
    private static void registerRenderLayers(Map instance, BiConsumer consumer) {
        // no-op so that we can manually add these after we've reinitialized the maps
    }

    /**
     * @reason avoid synchronized block for accessing render layer by instead using a read-write lock
     * @author Gegy
     */
    @Overwrite
    public static boolean canRenderInLayer(BlockState state, RenderType type) {
        Block block = state.getBlock();
        if (block instanceof LeavesBlock) {
            return fancyGraphics ? type == RenderType.getCutoutMipped() : type == RenderType.getSolid();
        }

        return canRenderInLayer(type, blockRenderChecks, block.delegate);
    }

    /**
     * @reason avoid synchronized block for accessing render layer by instead using a read-write lock
     * @author Gegy
     */
    @Overwrite
    public static boolean canRenderInLayer(FluidState fluid, RenderType type) {
        return canRenderInLayer(type, fluidRenderChecks, fluid.getFluid().delegate);
    }

    @Unique
    private static <T> boolean canRenderInLayer(RenderType type, Map<T, Predicate<RenderType>> map, T key) {
        StampedLock lock = RENDER_CHECK_LOCK;

        Predicate<RenderType> predicate;

        long stamp = lock.tryOptimisticRead();
        if (stamp != 0) {
            predicate = map.get(key);
            if (lock.validate(stamp)) {
                return predicate.test(type);
            }
        }

        stamp = lock.readLock();
        predicate = map.get(key);
        lock.unlockRead(stamp);

        return predicate.test(type);
    }

    /**
     * @reason avoid synchronized block for accessing render layer by instead using a read-write lock
     * @author Gegy
     */
    @Overwrite
    public static synchronized void setRenderLayer(Block block, Predicate<RenderType> predicate) {
        long stamp = RENDER_CHECK_LOCK.writeLock();
        try {
            blockRenderChecks.put(block.delegate, predicate);
        } finally {
            RENDER_CHECK_LOCK.unlockWrite(stamp);
        }
    }

    /**
     * @reason avoid synchronized block for accessing render layer by instead using a read-write lock
     * @author Gegy
     */
    @Overwrite
    public static synchronized void setRenderLayer(Fluid fluid, Predicate<RenderType> predicate) {
        long stamp = RENDER_CHECK_LOCK.writeLock();
        try {
            fluidRenderChecks.put(fluid.delegate, predicate);
        } finally {
            RENDER_CHECK_LOCK.unlockWrite(stamp);
        }
    }
}
