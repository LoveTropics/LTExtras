package com.lovetropics.extras.command.arguments;

import com.lovetropics.extras.model_modifer.ModelModifierType;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class ModelModifierArgumentType implements ArgumentType<ModelModifierType> {

    public static ModelModifierArgumentType modelModifier() {
        return new ModelModifierArgumentType();
    }

    private ModelModifierArgumentType() {}

    @Override
    public ModelModifierType parse(StringReader reader) throws CommandSyntaxException {
        return ModelModifierType.fromName(reader.readUnquotedString());
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(ModelModifierType.NAMES, builder);
    }

    @Override
    public Collection<String> getExamples() {
        return ModelModifierType.NAMES;
    }
}
