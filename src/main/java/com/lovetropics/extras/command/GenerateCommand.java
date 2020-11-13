package com.lovetropics.extras.command;

import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;

import net.minecraft.command.CommandSource;
import net.minecraft.item.Item;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.registries.ForgeRegistries;

public class GenerateCommand {

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private static final DynamicCommandExceptionType FAILED_TO_WRITE = new DynamicCommandExceptionType(o -> new StringTextComponent("Failed to write to file: " + o));

	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		// @formatter:off
		dispatcher.register(
			literal("generate").requires(source -> source.hasPermissionLevel(4))
				.then(literal("tag")
					.then(literal("item")
						.then(argument("name", StringArgumentType.word())
							.then(argument("pattern", StringArgumentType.greedyString())
								.executes(GenerateCommand::generateItemTag))))));
		// @formatter:on
	}

	private static int generateItemTag(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
		Pattern pattern = Pattern.compile(StringArgumentType.getString(ctx, "pattern"));

		Tag.Builder<Item> tagBuilder = new Tag.Builder<>();
		for (Entry<ResourceLocation, Item> e : ForgeRegistries.ITEMS.getEntries()) {
			if (pattern.matcher(e.getKey().toString()).matches()) {
				tagBuilder.add(e.getValue());
			}
		}
		TagCollection<Item> tagcollection = new TagCollection<>($ -> Optional.empty(), "", false, "generated");
		tagcollection.registerAll(Maps.newHashMap(ImmutableMap.of(new ResourceLocation("generated"), tagBuilder)));
		JsonObject json = tagcollection.getTagMap().values().iterator().next().serialize(ForgeRegistries.ITEMS::getKey);

		Path output = Paths.get("export", "generated", "tags", "item", StringArgumentType.getString(ctx, "name") + ".json");
		try {
			Files.createDirectories(output.getParent());
			Files.write(output, Collections.singleton(GSON.toJson(json)), StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e1) {
			e1.printStackTrace();
			throw FAILED_TO_WRITE.create(e1);
		}
		return Command.SINGLE_SUCCESS;
	}
}
