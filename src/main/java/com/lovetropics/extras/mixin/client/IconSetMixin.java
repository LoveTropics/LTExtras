package com.lovetropics.extras.mixin.client;

import com.lovetropics.extras.LTExtras;
import com.mojang.blaze3d.platform.IconSet;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.InputStream;

@Mixin(IconSet.class)
public class IconSetMixin {
    @Redirect(method = "getFile", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/PackResources;getRootResource([Ljava/lang/String;)Lnet/minecraft/server/packs/resources/IoSupplier;"))
    @Nullable
    private IoSupplier<InputStream> getResource(PackResources resources, String[] path) {
        if (path[path.length - 1].endsWith(".icns")) {
            return resources.getRootResource(path);
        }
        return () -> LTExtras.class.getResourceAsStream("/" + String.join("/", path));
    }
}
