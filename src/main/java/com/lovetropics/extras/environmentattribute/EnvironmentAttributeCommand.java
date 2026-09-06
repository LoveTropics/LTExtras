package com.lovetropics.extras.environmentattribute;

import com.lovetropics.extras.LTExtras;
import com.lovetropics.extras.data.Named;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.attribute.modifier.AttributeModifier;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;
import static net.minecraft.commands.arguments.IdentifierArgument.getId;
import static net.minecraft.commands.arguments.IdentifierArgument.id;
import static net.minecraft.commands.arguments.NbtTagArgument.getNbtTag;
import static net.minecraft.commands.arguments.NbtTagArgument.nbtTag;
import static net.minecraft.commands.arguments.ResourceArgument.getResource;
import static net.minecraft.commands.arguments.ResourceArgument.resource;
import static net.minecraft.commands.arguments.TimeArgument.time;

public class EnvironmentAttributeCommand {
    private static final Identifier DEFAULT_LAYER = LTExtras.id("default");

    private static final DynamicCommandExceptionType BUNDLE_NOT_FOUND = new DynamicCommandExceptionType(id ->
            Component.literal("Environment Attribute Bundle not exist with id: " + id)
    );
    private static final DynamicCommandExceptionType OPERATION_NOT_FOUND = new DynamicCommandExceptionType(id ->
            Component.literal(id + " is not a valid operation")
    );
    private static final DynamicCommandExceptionType MALFORMED_VALUE = new DynamicCommandExceptionType(message ->
            Component.literal("Value is malformed: " + message)
    );

    private static final StringRepresentable.EnumCodec<AttributeModifier.OperationId> OPERATION_CODEC = StringRepresentable.fromEnum(AttributeModifier.OperationId::values);

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
        LiteralArgumentBuilder<CommandSourceStack> command = literal("environmentattribute")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(literal("layer")
                        .then(layerCommands(
                                buildContext,
                                context -> getId(context, "layer"),
                                argument("layer", id())
                                        .suggests((_, builder) -> SharedSuggestionProvider.suggestResource(
                                                ServerDynamicEasManager.streamLayerIds(),
                                                builder
                                        ))
                        ))
                )
                .then(literal("enable")
                        .then(bundleArgument("bundle")
                                .then(argument("transition", time())
                                        .executes(context ->
                                                enableBundle(context, getBundle(context, "bundle"), getInteger(context, "transition"))
                                        )
                                )
                                .executes(context ->
                                        enableBundle(context, getBundle(context, "bundle"), 0)
                                )
                        )
                )
                .then(literal("disable")
                        .then(bundleArgument("bundle")
                                .then(argument("transition", time())
                                        .executes(context ->
                                                disableBundle(context, getBundle(context, "bundle"), getInteger(context, "transition"))
                                        )
                                )
                                .executes(context ->
                                        disableBundle(context, getBundle(context, "bundle"), 0)
                                )
                        )
                );
        command = layerCommands(
                buildContext,
                _ -> DEFAULT_LAYER,
                command
        );
        dispatcher.register(command);
    }

    @SuppressWarnings("unchecked")
    private static <A extends ArgumentBuilder<CommandSourceStack, ?>> A layerCommands(CommandBuildContext buildContext, LayerGetter layerGetter, A argument) {
        return (A) argument
                .then(literal("set")
                        .then(argument("attribute", resource(buildContext, Registries.ENVIRONMENT_ATTRIBUTE))
                                .then(argument("value", nbtTag())
                                        .then(argument("transition", time())
                                                .executes(context ->
                                                        setAttribute(context, layerGetter.get(context), getInteger(context, "transition"))
                                                )
                                        )
                                        .executes(context ->
                                                setAttribute(context, layerGetter.get(context), 0)
                                        )
                                )
                        )
                )
                .then(literal("modify")
                        .then(argument("attribute", resource(buildContext, Registries.ENVIRONMENT_ATTRIBUTE))
                                .then(argument("operation", word())
                                        .suggests((context, builder) ->
                                                suggestModifiers(builder, getAttribute(context, "attribute"))
                                        )
                                        .then(argument("value", nbtTag())
                                                .then(argument("transition", time())
                                                        .executes(context ->
                                                                modifyAttribute(context, layerGetter.get(context), getInteger(context, "transition"))
                                                        )
                                                )
                                                .executes(context ->
                                                        modifyAttribute(context, layerGetter.get(context), 0)
                                                )
                                        )
                                )
                        )
                )
                .then(literal("clear")
                        .then(argument("attribute", resource(buildContext, Registries.ENVIRONMENT_ATTRIBUTE))
                                .then(argument("transition", time())
                                        .executes(context ->
                                                clearAttribute(context, layerGetter, getInteger(context, "transition"))
                                        )
                                )
                                .executes(context ->
                                        clearAttribute(context, layerGetter, 0)
                                )
                        )
                        .then(argument("transition", time())
                                .executes(context ->
                                        clearAttributes(context, layerGetter, getInteger(context, "transition"))
                                )
                        )
                        .executes(context ->
                                clearAttributes(context, layerGetter, 0)
                        )
                );
    }

    private static int modifyAttribute(CommandContext<CommandSourceStack> context, Identifier layerId, int transitionTicks) throws CommandSyntaxException {
        EnvironmentAttribute<?> attribute = getAttribute(context, "attribute");
        return modifyAttribute(context, attribute, layerId, transitionTicks);
    }

    private static int setAttribute(CommandContext<CommandSourceStack> context, Identifier layerId, int transitionTicks) throws CommandSyntaxException {
        EnvironmentAttribute<?> attribute = getAttribute(context, "attribute");
        return modifyAttribute(context, attribute, layerId, AttributeModifier.override(), transitionTicks);
    }

    private static <Value> int modifyAttribute(CommandContext<CommandSourceStack> context, EnvironmentAttribute<Value> attribute, Identifier layerId, int transitionTicks) throws CommandSyntaxException {
        AttributeModifier<Value, ?> modifier = getModifier(context, attribute, "operation");
        return modifyAttribute(context, attribute, layerId, modifier, transitionTicks);
    }

    private static <Value, Argument> int modifyAttribute(CommandContext<CommandSourceStack> context, EnvironmentAttribute<Value> attribute, Identifier layerId, AttributeModifier<Value, Argument> modifier, int transitionTicks) throws CommandSyntaxException {
        RegistryOps<Tag> ops = context.getSource().registryAccess().createSerializationContext(NbtOps.INSTANCE);
        Argument argument = modifier.argumentCodec(attribute)
                .parse(ops, getNbtTag(context, "value"))
                .getOrThrow(MALFORMED_VALUE::create);
        ServerDynamicEasManager.modifyAttribute(context.getSource().getLevel(), layerId, attribute, modifier, argument, transitionTicks);
        return 1;
    }

    private static int clearAttributes(CommandContext<CommandSourceStack> context, LayerGetter layerGetter, int transitionTicks) throws CommandSyntaxException {
        int count = 0;
        for (EnvironmentAttribute<?> attribute : BuiltInRegistries.ENVIRONMENT_ATTRIBUTE) {
            if (ServerDynamicEasManager.clearAttribute(context.getSource().getLevel(), layerGetter.get(context), attribute, transitionTicks)) {
                count++;
            }
        }
        return count;
    }

    private static int clearAttribute(CommandContext<CommandSourceStack> context, LayerGetter layerGetter, int transitionTicks) throws CommandSyntaxException {
        EnvironmentAttribute<?> attribute = getAttribute(context, "attribute");
        return ServerDynamicEasManager.clearAttribute(context.getSource().getLevel(), layerGetter.get(context), attribute, transitionTicks) ? 1 : 0;
    }

    private static int enableBundle(CommandContext<CommandSourceStack> context, Named<EnvironmentAttributeMap> bundle, int transitionTicks) {
        ServerLevel level = context.getSource().getLevel();
        Identifier layerId = bundleToLayerId(bundle);
        EnvironmentAttributeMap attributes = bundle.value();
        for (EnvironmentAttribute<?> attribute : attributes.keySet()) {
            modifyAttributeFromMap(level, layerId, attributes, attribute, transitionTicks);
        }
        return 1;
    }

    private static <Value> void modifyAttributeFromMap(ServerLevel level, Identifier layerId, EnvironmentAttributeMap map, EnvironmentAttribute<Value> attribute, int transitionTicks) {
        modifyAttributeByEntry(level, layerId, attribute, Objects.requireNonNull(map.get(attribute)), transitionTicks);
    }

    private static <Value, Argument> void modifyAttributeByEntry(ServerLevel level, Identifier layerId, EnvironmentAttribute<Value> attribute, EnvironmentAttributeMap.Entry<Value, Argument> entry, int transitionTicks) {
        ServerDynamicEasManager.modifyAttribute(level, layerId, attribute, entry.modifier(), entry.argument(), transitionTicks);
    }

    private static int disableBundle(CommandContext<CommandSourceStack> context, Named<EnvironmentAttributeMap> bundle, int transitionTicks) {
        ServerLevel level = context.getSource().getLevel();
        Identifier layerId = bundleToLayerId(bundle);
        EnvironmentAttributeMap attributes = bundle.value();
        for (EnvironmentAttribute<?> attribute : attributes.keySet()) {
            ServerDynamicEasManager.clearAttribute(level, layerId, attribute, transitionTicks);
        }
        return 1;
    }

    private static Identifier bundleToLayerId(Named<EnvironmentAttributeMap> bundle) {
        return bundle.key().identifier().withPrefix("bundle/");
    }

    private static RequiredArgumentBuilder<CommandSourceStack, Identifier> bundleArgument(String name) {
        return argument(name, id()).suggests((_, builder) -> SharedSuggestionProvider.suggestResource(
                EnvironmentAttributeBundleConfigs.REGISTRY.stream().map(Named::id),
                builder
        ));
    }

    private static Named<EnvironmentAttributeMap> getBundle(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
        Identifier id = IdentifierArgument.getId(context, name);
        Named<EnvironmentAttributeMap> config = EnvironmentAttributeBundleConfigs.REGISTRY.get(id);
        if (config == null) {
            throw BUNDLE_NOT_FOUND.create(id);
        }
        return config;
    }

    private static <Value> AttributeModifier<Value, ?> getModifier(CommandContext<CommandSourceStack> context, EnvironmentAttribute<Value> attribute, String name) throws CommandSyntaxException {
        String id = getString(context, name);
        AttributeModifier.OperationId operationId = OPERATION_CODEC.byName(id);
        if (operationId == null) {
            throw OPERATION_NOT_FOUND.create(id);
        }
        AttributeModifier<Value, ?> modifier = attribute.type().modifierLibrary().get(operationId);
        if (modifier == null) {
            throw OPERATION_NOT_FOUND.create(id);
        }
        return modifier;
    }

    private static EnvironmentAttribute<?> getAttribute(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
        return getResource(context, name, Registries.ENVIRONMENT_ATTRIBUTE).value();
    }

    private static CompletableFuture<Suggestions> suggestModifiers(SuggestionsBuilder builder, EnvironmentAttribute<?> attribute) {
        Set<AttributeModifier.OperationId> availableOperations = attribute.type().modifierLibrary().keySet();
        return SharedSuggestionProvider.suggest(
                availableOperations.stream().map(AttributeModifier.OperationId::getSerializedName),
                builder
        );
    }

    @FunctionalInterface
    private interface LayerGetter {
        Identifier get(CommandContext<CommandSourceStack> context) throws CommandSyntaxException;
    }
}
