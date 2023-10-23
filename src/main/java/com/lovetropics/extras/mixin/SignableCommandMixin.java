package com.lovetropics.extras.mixin;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.tree.RootCommandNode;
import net.minecraft.network.chat.SignableCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(SignableCommand.class)
public abstract class SignableCommandMixin {
    @Shadow
    private static <S> List<SignableCommand.Argument<S>> collectArguments(final String key, final CommandContextBuilder<S> context) {
        throw new UnsupportedOperationException();
    }

    /**
     * @author Gegy
     * @reason Hackfix for Forge issue with client commands causing /execute subcommands to be signed
     * <p>
     * Does not need to be an overwrite, but I'm tired.
     */
    @Overwrite
    public static <S> SignableCommand<S> of(final ParseResults<S> parse) {
        final String string = parse.getReader().getString();
        final CommandContextBuilder<S> rootContext = parse.getContext();

        final List<SignableCommand.Argument<S>> arguments = collectArguments(string, rootContext);

        CommandContextBuilder<S> context = rootContext;
        CommandContextBuilder<S> child;
        while ((child = context.getChild()) != null) {
            // This is the actual patch - /execute redirects to the server-only root, so we can't expect them to be equal
            if (child.getRootNode() instanceof RootCommandNode<S> && rootContext.getRootNode() instanceof RootCommandNode<S>) {
                break;
            }
            arguments.addAll(collectArguments(string, child));
            context = child;
        }

        return new SignableCommand<>(arguments);
    }
}
