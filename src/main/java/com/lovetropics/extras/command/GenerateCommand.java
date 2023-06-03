package com.lovetropics.extras.command;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lovetropics.extras.LTExtras;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.serialization.JsonOps;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagFile;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class GenerateCommand {

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private static final DynamicCommandExceptionType FAILED_TO_WRITE = new DynamicCommandExceptionType(o -> Component.literal("Failed to write to file: " + o));

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		// @formatter:off
        dispatcher.register(
            literal("generate").requires(source -> source.hasPermission(4))
                .then(literal("tag")
                    .then(literal("item")
                        .then(argument("name", StringArgumentType.word())
                            .then(argument("pattern", StringArgumentType.greedyString())
                                .executes(GenerateCommand::generateItemTag))))));
        // @formatter:on
	}

	private static int generateItemTag(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		Pattern pattern = Pattern.compile(StringArgumentType.getString(ctx, "pattern"));

		TagBuilder tagBuilder = TagBuilder.create();

		for (Entry<ResourceKey<Item>, Item> e : ForgeRegistries.ITEMS.getEntries()) {
			ResourceLocation id = e.getKey().location();
			if (pattern.matcher(id.toString()).matches()) {
				tagBuilder.addElement(id);
			}
		}

		JsonElement json = TagFile.CODEC.encodeStart(JsonOps.INSTANCE, new TagFile(tagBuilder.build(), false)).getOrThrow(false, System.err::println);

		Path output = Paths.get("export", "generated", "tags", "item", StringArgumentType.getString(ctx, "name") + ".json");
		try {
			Files.createDirectories(output.getParent());
			Files.write(output, Collections.singleton(GSON.toJson(json)), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e1) {
			e1.printStackTrace();
			throw FAILED_TO_WRITE.create(e1);
		}
		return Command.SINGLE_SUCCESS;
	}
}
