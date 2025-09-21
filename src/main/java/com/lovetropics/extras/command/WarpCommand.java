package com.lovetropics.extras.command;

import com.lovetropics.extras.data.Named;
import com.lovetropics.extras.data.poi.MapManager;
import com.lovetropics.extras.data.poi.PoiConfig;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import java.util.Set;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class WarpCommand {

    private static final String ARGUMENT_TARGET = "target";
    private static final SimpleCommandExceptionType NOT_FOUND = new SimpleCommandExceptionType(Component.translatable("commands.warp.not_found"));

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        // @formatter:off
		dispatcher.register(literal("warp")
				.then(argument(ARGUMENT_TARGET, ResourceLocationArgument.id())
                        .suggests((context, builder) -> {
                            MapManager poiManager = MapManager.get(context.getSource().getServer());
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            return SharedSuggestionProvider.suggestResource(poiManager.getAccessiblePois(player).map(ResourceKey::location), builder);
                        })
						.executes(WarpCommand::warp)
		));
        // @formatter:on
    }

    private static int warp(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ResourceLocation targetName = ResourceLocationArgument.getId(ctx, ARGUMENT_TARGET);
        CommandSourceStack source = ctx.getSource();
        ServerPlayer player = source.getPlayerOrException();

        Named<PoiConfig> target = MapManager.get(source.getServer()).getPoiAccessibleTo(player, targetName);
        if (target == null) {
            throw NOT_FOUND.create();
        }

        BlockPos blockPos = target.value().pos();
        ServerLevel level = player.getServer().getLevel(target.value().map().value().dimension());
        if (level == null) {
            throw NOT_FOUND.create();
        }

        float yRot = target.value().angle().orElse(player.getYRot());
        player.teleportTo(level, blockPos.getX() + 0.5, blockPos.getY() + 0d, blockPos.getZ() + 0.5,
                Set.of(), yRot, player.getXRot(), true);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.0f);

        source.sendSuccess(() -> Component.translatable("commands.warp.success", target.value().description()), false);
        return Command.SINGLE_SUCCESS;
    }

    public static void addTranslations(RegistrateLangProvider provider) {
        provider.add("commands.warp.success", "Warped to %s");
        provider.add("commands.warp.not_found", "Destination not found");
    }
}
