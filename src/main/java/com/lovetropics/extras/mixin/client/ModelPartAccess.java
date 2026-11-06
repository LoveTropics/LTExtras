package com.lovetropics.extras.mixin.client;

import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(ModelPart.class)
public interface ModelPartAccess {
    @Accessor("children")
    Map<String, ModelPart> lt$children();

    @Accessor("cubes")
    List<ModelPart.Cube> lt$cubes();
}
