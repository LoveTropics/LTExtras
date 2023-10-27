package com.lovetropics.extras.mixin.fix;

import com.mojang.brigadier.StringReader;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ComponentArgument.class)
public class ComponentArgumentMixin {
    @Inject(method = "parse(Lcom/mojang/brigadier/StringReader;)Lnet/minecraft/network/chat/Component;", at = @At("RETURN"))
    private void parse(final StringReader reader, final CallbackInfoReturnable<Component> cir) {
        // The Component parser reads one character too many such that the argument separator gets missed
        // This means that the argument cannot be used in the middle of a command
        reader.setCursor(reader.getCursor() - 1);
    }
}
