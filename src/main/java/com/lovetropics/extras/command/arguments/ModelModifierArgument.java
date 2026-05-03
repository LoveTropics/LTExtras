package com.lovetropics.extras.command.arguments;

import com.lovetropics.extras.model_modifer.ExtraModelModifierTypes;
import com.lovetropics.extras.model_modifer.ModelModifier;
import com.lovetropics.extras.registry.ExtraRegistries;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceOrIdArgument;
import net.minecraft.core.Holder;

public class ModelModifierArgument extends ResourceOrIdArgument<ModelModifier<?>> {

    protected ModelModifierArgument(CommandBuildContext context) {
        super(context, ExtraRegistries.MODEL_MODIFIER, ExtraModelModifierTypes.CODEC);
    }

    public static ModelModifierArgument modifier(CommandBuildContext context) {
        return new ModelModifierArgument(context);
    }

    public static Holder<ModelModifier<?>> getModifier(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
        return getResource(context, name);
    }
}
