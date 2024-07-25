package com.lovetropics.extras.mixin;

import com.google.common.collect.ImmutableList;
import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.api.IEnvironment;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class LTExtrasMixinPlugin implements IMixinConfigPlugin {
	private static final String MIXIN_PACKAGE = "com.lovetropics.extras.mixin";
	private static final String MIXIN_CLIENT_PERF_PACKAGE = MIXIN_PACKAGE + ".client.perf";

	@Override
	public void onLoad(String mixinPackage) {
	}

	@Override
	@Nullable
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		if (mixinClassName.startsWith(MIXIN_CLIENT_PERF_PACKAGE)) {
			return !isOptifineLoaded();
		}

		return true;
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
	}

	@Override
	@Nullable
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
		List<Map<String, String>> maps = Launcher.INSTANCE.environment().getProperty(IEnvironment.Keys.MODLIST.get())
				.orElse(ImmutableList.of());

		for (Map<String, String> map : maps) {
			if ("optifine".equalsIgnoreCase(map.get("name"))) {
				return true;
			}
		}

		return false;
	}
}
