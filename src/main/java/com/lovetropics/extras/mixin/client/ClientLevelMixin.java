package com.lovetropics.extras.mixin.client;

import com.lovetropics.extras.duck.HasDynamicEas;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin implements HasDynamicEas {
    @Final
    @Shadow
    @Mutable
    private EnvironmentAttributeSystem environmentAttributes;

    @Unique
    private final List<Decorator> ltextras$easDecorators = new ArrayList<>();

    @Shadow
    protected abstract EnvironmentAttributeSystem.Builder addEnvironmentAttributeLayers(EnvironmentAttributeSystem.Builder environmentAttributes);

    @Inject(method = "addEnvironmentAttributeLayers", at = @At("TAIL"))
    private void decorateEasLayers(EnvironmentAttributeSystem.Builder environmentAttributes, CallbackInfoReturnable<EnvironmentAttributeSystem.Builder> cir) {
        for (Decorator decorator : ltextras$easDecorators) {
            decorator.addTo(environmentAttributes);
        }
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
        environmentAttributes = addEnvironmentAttributeLayers(EnvironmentAttributeSystem.builder()).build();
    }
}
