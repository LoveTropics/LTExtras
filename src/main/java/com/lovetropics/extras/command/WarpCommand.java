package com.lovetropics.extras.command;

import com.lovetropics.extras.data.poi.MapPoiManager;
import com.lovetropics.extras.data.poi.Poi;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class WarpCommand {

    private static final String ARGUMENT_TARGET = "target";
    private static final SimpleCommandExceptionType GENERAL_ERROR = new SimpleCommandExceptionType(Component.translatable("commands.warp.general_error"));
    private static final SimpleCommandExceptionType NOT_FOUND = new SimpleCommandExceptionType(Component.translatable("commands.warp.not_found"));

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        // @formatter:off
		dispatcher.register(literal("warp")
				.then(argument(ARGUMENT_TARGET, StringArgumentType.word())
                        .suggests((context, builder) -> {
                            MapPoiManager poiManager = MapPoiManager.get(context.getSource().getServer());
                            return SharedSuggestionProvider.suggest(poiManager.getEnabledPois().stream().map(Poi::name), builder);
                        })
						.executes(WarpCommand::warp)
		));
        // @formatter:on
    }

    private static int warp(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        String targetName = ctx.getArgument(ARGUMENT_TARGET, String.class);
        Poi target = MapPoiManager.get(ctx.getSource().getServer()).getPoi(targetName);
        if (target == null) {
            throw NOT_FOUND.create();
        }
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        BlockPos blockPos = target.globalPos().pos();
        ServerLevel level = player.getServer().getLevel(target.globalPos().dimension());

        if (!target.enabled() && !player.hasPermissions(Commands.LEVEL_GAMEMASTERS)) {
            throw GENERAL_ERROR.create();
        }

        player.teleportTo(level, blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, player.getYRot(), player.getXRot());
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.0f);

        ctx.getSource().sendSuccess(() -> Component.translatable("commands.warp.success", target.description()), false);
        return Command.SINGLE_SUCCESS;
    }

    public static void addTranslations(RegistrateLangProvider provider) {
        provider.add("commands.warp.general_error", "Couldn't go there");
        provider.add("commands.warp.success", "Warped to %s");
        provider.add("commands.warp.not_found", "Destination not found");
    }
}
