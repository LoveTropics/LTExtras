package com.lovetropics.extras.mixin;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public final class LTExtrasMixinPlugin implements IMixinConfigPlugin {
    private static final String MIXIN_PACKAGE = "com.lovetropics.extras.mixin";
    private static final String MIXIN_CLIENT_PERF_PACKAGE = MIXIN_PACKAGE + ".client.perf";

    private static final boolean APPLY_CLIENT_PERF = !isOptifineLoaded();

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.startsWith(MIXIN_CLIENT_PERF_PACKAGE)) {
            return APPLY_CLIENT_PERF;
        }

        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    private static boolean isOptifineLoaded() {
        try {
            Class.forName("net.optifine.Config");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
