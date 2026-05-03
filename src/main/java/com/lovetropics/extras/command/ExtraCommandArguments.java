package com.lovetropics.extras.command;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.command.arguments.ModelModifierArgument;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ExtraCommandArguments {
    public static final DeferredRegister<ArgumentTypeInfo<?, ?>> REGISTER = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, LTExtras.MODID);

    private static final Holder<ArgumentTypeInfo<?, ?>> MODEL_MODIFIER = REGISTER.register("model_modifier", () -> ArgumentTypeInfos.registerByClass(ModelModifierArgument.class, SingletonArgumentInfo.contextAware(ModelModifierArgument::modifier)));
}
