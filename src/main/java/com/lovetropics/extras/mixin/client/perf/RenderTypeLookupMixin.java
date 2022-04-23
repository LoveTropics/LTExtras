package com.lovetropics.extras.mixin.client.perf;

import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
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
@Mixin(ItemBlockRenderTypes.class)
public class RenderTypeLookupMixin {
    @Shadow private static boolean renderCutout;

    @Shadow @Final @Deprecated private static Map<Block, RenderType> TYPE_BY_BLOCK;
    @Shadow @Final @Deprecated private static Map<Fluid, RenderType> TYPE_BY_FLUID;
    @Shadow(remap = false)
    @Mutable @Final private static Map<IRegistryDelegate<Block>, Predicate<RenderType>> blockRenderChecks;
    @Shadow(remap = false)
    @Mutable @Final private static Map<IRegistryDelegate<Fluid>, Predicate<RenderType>> fluidRenderChecks;

    @Unique
    private static final StampedLock RENDER_CHECK_LOCK = new StampedLock();

    @Unique
    private static final Predicate<RenderType> SOLID_PREDICATE = type -> type == RenderType.solid();

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void init(CallbackInfo ci) {
        Reference2ObjectMap<IRegistryDelegate<Block>, Predicate<RenderType>> blockMap = new Reference2ObjectOpenHashMap<>();
        Reference2ObjectMap<IRegistryDelegate<Fluid>, Predicate<RenderType>> fluidMap = new Reference2ObjectOpenHashMap<>();
        blockMap.defaultReturnValue(SOLID_PREDICATE);
        fluidMap.defaultReturnValue(SOLID_PREDICATE);

        RenderTypeLookupMixin.blockRenderChecks = blockMap;
        RenderTypeLookupMixin.fluidRenderChecks = fluidMap;

        TYPE_BY_BLOCK.forEach(ItemBlockRenderTypes::setRenderLayer);
        TYPE_BY_FLUID.forEach(ItemBlockRenderTypes::setRenderLayer);
    }

    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Ljava/util/Map;forEach(Ljava/util/function/BiConsumer;)V"))
    private static void registerRenderLayers(Map instance, BiConsumer consumer) {
        // no-op so that we can manually add these after we've reinitialized the maps
    }

    /**
     * @reason avoid synchronized block for accessing render layer by instead using a read-write lock
     * @author Gegy
     */
    @Overwrite(remap = false)
    public static boolean canRenderInLayer(BlockState state, RenderType type) {
        Block block = state.getBlock();
        if (block instanceof LeavesBlock) {
            return renderCutout ? type == RenderType.cutoutMipped() : type == RenderType.solid();
        }

        return canRenderInLayer(type, blockRenderChecks, block.delegate);
    }

    /**
     * @reason avoid synchronized block for accessing render layer by instead using a read-write lock
     * @author Gegy
     */
    @Overwrite(remap = false)
    public static boolean canRenderInLayer(FluidState fluid, RenderType type) {
        return canRenderInLayer(type, fluidRenderChecks, fluid.getType().delegate);
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
    @Overwrite(remap = false)
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
    @Overwrite(remap = false)
    public static synchronized void setRenderLayer(Fluid fluid, Predicate<RenderType> predicate) {
        long stamp = RENDER_CHECK_LOCK.writeLock();
        try {
            fluidRenderChecks.put(fluid.delegate, predicate);
        } finally {
            RENDER_CHECK_LOCK.unlockWrite(stamp);
        }
    }
}
