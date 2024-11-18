package com.lovetropics.extras.client.command;

import com.lovetropics.extras.LTExtras;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderNameTagEvent;
import net.neoforged.neoforge.common.util.TriState;

import java.util.Arrays;

import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

@EventBusSubscriber(modid = LTExtras.MODID, value = Dist.CLIENT)
public class NameTagModeCommand {
	private static Mode mode = Mode.DEFAULT;

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(literal("nameTagMode")
				.then(argument("mode", word())
						.suggests((context, builder) -> SharedSuggestionProvider.suggest(Arrays.stream(Mode.values()).map(Mode::getSerializedName), builder))
						.executes(NameTagModeCommand::setNameTagsMode)
				));
	}

	public static int setNameTagsMode(CommandContext<CommandSourceStack> ctx) {
		mode = Mode.CODEC.byName(StringArgumentType.getString(ctx, "mode"), Mode.DEFAULT);
		ctx.getSource().sendSuccess(() -> Component.literal("Name tag mode: " + mode.getSerializedName()), false);
		return Command.SINGLE_SUCCESS;
	}

	@SubscribeEvent
	public static void onRenderNameTagEvent(RenderNameTagEvent evt) {
		switch (mode) {
			case NONE -> evt.setCanRender(TriState.FALSE);
			case ALL_PLAYERS -> {
				if (evt.getEntity() instanceof Player) {
					evt.setCanRender(TriState.TRUE);
				}
			}
			case ALL -> evt.setCanRender(TriState.TRUE);
		}
	}

	public enum Mode implements StringRepresentable {
		DEFAULT("default"),
		NONE("none"),
		ALL_PLAYERS("all_players"),
		ALL("all"),
		;

		public static final EnumCodec<Mode> CODEC = StringRepresentable.fromEnum(Mode::values);

		private final String id;

		Mode(String id) {
			this.id = id;
		}

		@Override
		public String getSerializedName() {
			return id;
		}
	}
}
