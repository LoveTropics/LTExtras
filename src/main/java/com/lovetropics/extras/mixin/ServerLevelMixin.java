package com.lovetropics.extras.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.lovetropics.extras.duck.HasDynamicEas;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;

@Mixin(ServerLevel.class)
public class ServerLevelMixin implements HasDynamicEas {
    @Shadow
    private EnvironmentAttributeSystem environmentAttributes;

    @Unique
    private final List<Decorator> ltextras$easDecorators = new ArrayList<>();

    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/attribute/EnvironmentAttributeSystem$Builder;addDefaultLayers(Lnet/minecraft/world/level/Level;)Lnet/minecraft/world/attribute/EnvironmentAttributeSystem$Builder;"))
    private EnvironmentAttributeSystem.Builder decorateEasLayers(EnvironmentAttributeSystem.Builder instance, Level level, Operation<EnvironmentAttributeSystem.Builder> original) {
        EnvironmentAttributeSystem.Builder system = original.call(instance, level);
        for (Decorator decorator : ltextras$easDecorators) {
            decorator.addTo(system);
        }
        return system;
    }

    @Override
    public DecoratorHandle ltextras$addEasDecorator(Decorator decorator) {
        ltextras$easDecorators.add(decorator);
        ltextras$rebuildEas();
        return new DecoratorHandle() {
            @Override
            public void rebuild() {
                ltextras$rebuildEas();
            }

            @Override
            public void close() {
                ltextras$easDecorators.remove(decorator);
                ltextras$rebuildEas();
            }
        };
    }

    @Unique
    private void ltextras$rebuildEas() {
        EnvironmentAttributeSystem.Builder system = EnvironmentAttributeSystem.builder()
                .addDefaultLayers((ServerLevel) (Object) this);
        for (Decorator decorator : ltextras$easDecorators) {
            decorator.addTo(system);
        }
        environmentAttributes = system.build();
    }
}
